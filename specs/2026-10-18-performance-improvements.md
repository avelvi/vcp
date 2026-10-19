# Spec: Performance improvements (read path, front-end load, view counting, async upload, platform)
Spec ID: SPEC-2026-10-18-performance-improvements
Status: draft
Supersedes: none

## Problem and user
The Video Content Portal (Spring MVC 4.2.5 WAR, MongoDB, Elasticsearch embedded in the same JVM, AngularJS front end served by the same WAR) gets slower as the number of videos and users grows. Anonymous visitors, registered users who upload, and admins are affected. The request was "write spec what can be changed for better performance". Every item below is based on evidence in the code (file:line).

Backend findings (the evidence):
- N+1 reads on every video list. `Video.owner` and `Video.category` are lazy DBRefs (`model/Video.java:28-31`). `GravatarAspect.videosAdvice` touches every owner in the page (`aop/GravatarAspect.java:42-55,66-67`), and JSON serialization loads each category. This affects GET /videos (12 per page), /videos/latest, /videos/popular, /search and a user's video list.
- No index on `Video` at all. Only unique indexes exist on User, Category, Company and Authority (grep of `@Indexed`). "Latest" sorts on `createdDate` and "Popular" on `views` (`repository/storage/VideoRepository.java:15-17`). `findByOwnerId` is unindexed too. `createdDate` is a String of epoch milliseconds (`service/impl/VideoProcessorServiceImpl.java:44-45`).
- No index on `VideoStatistic` for `findByVideoIdAndIpAddress` (`repository/storage/VideoStatisticRepository.java:11`), so each counted view scans the collection.
- View counting reads the whole Video, increments, and saves the whole document (`service/impl/VideoStatisticServiceImpl.java:32-40`). Concurrent views are lost and the Elasticsearch copy is not updated.
- Page size is chosen by the client with no explicit maximum (`config/WebMvcConfig.java:23`, `controller/VideoController.java:32,60,66`). Every page also runs a count. "Load more" uses skip-based paging (`public/video/list.controller.js:28-44`).
- No gzip, no `Cache-Control`, no cache period on `/static`, `/partials` or `/media` (`config/WebMvcConfig.java:44-48`), no `@Cacheable` and no `@EnableCaching` anywhere. Videos are served by the Spring resource handler (`WebMvcConfig.java:46`) on Spring 4.2.5 (`pom.xml:11`). Range support there is unconfirmed.
- Upload runs the whole pipeline in one request thread: temp-file transfer, full copy, JCodec frame decode, then Mongo and Elasticsearch writes (`aop/UploadVideoTempStorage.java:31-53`, `service/impl/VideoServiceImpl.java:149-154`, `service/impl/JCodecThumbnailService.java:40-56`, `service/impl/UserServiceImpl.java:65-72`). The `FileChannel` opened for the thumbnail is never closed (`JCodecThumbnailService.java:54`).
- Thumbnails are the full-resolution first frame (`JCodecThumbnailService.java:46-48`). Avatars are stored exactly as uploaded (`service/impl/UserServiceImpl.java:262-263`) but shown at `small-avatar` size.
- Startup drops the video index, loads all videos with `findAll()` and indexes them one by one. This is on by default (`application.properties:7`, `service/impl/ElasticSearchIndexingService.java:34-46`).
- Videos are indexed with the full embedded owner (`service/impl/VideoServiceImpl.java:110,115`, `UserServiceImpl.java:71`). No `@JsonIgnore`/`@JsonView` exists in the project, so owner password hash, email and recovery code reach Elasticsearch and the API payload (see Open questions #11).

Front-end findings:
- `index.html` loads about 45 blocking script tags, unminified `angular.js` and `bootstrap.css`, every admin module for every visitor, and ui-bootstrap from a CDN (`index.html:6-127`, line 28). The navbar is a separate ng-include request (line 12).
- The home page makes 3 API calls (`public/video/list.controller.js:7-24`) and renders up to 18 `<video>` elements with no `preload` attribute (`public/video/list.html:8,32,56`). "Load more" appends without limit.
- Every card loads its avatar from gravatar.com with a fallback to our server (`GravatarAspect.java:69-70`).

## Goals / Non-goals
Goals (tiered; the tier order is the priority order):
- Tier 1 - fast read path: bounded, index-backed, cached list endpoints; atomic and cheap view counting; compressed and cacheable static and media responses; a lighter home page.
- Tier 2 - upload and startup: upload returns quickly while thumbnail creation and indexing finish in the background; startup reindex is fast and avoidable.
- Tier 3 - platform and infrastructure (all in scope per the user): upgrade Java, Spring and libraries; replace AngularJS; shared cache, CDN and video transcoding; remove sensitive data from the search index.
- Measured against these budgets, at the assumed scale of 10,000 videos and 1,000 users on dev hardware: p95 of 300 ms or less for /videos, /videos/latest, /videos/popular and /search; at most 3 Mongo queries per list request; home page at most 15 requests and 1 MB before first interaction with no video preload; startup reindex at most 60 s; a 100 MB upload returns in under 5 s.

Non-goals:
- Changing the JSON field names or shapes of existing endpoints, or the `page`/`size` parameters (a maximum for `size` is added). `owner.avatar` stays a URL.
- Changing the 500 MB per-file upload limit (`WebMvcConfig.java:37`).
- Visual redesign or new user-facing features (only the loading behaviour changes).
- Choosing specific products. The replacement front-end framework, target Java/Spring versions, CDN vendor and shared-cache product are left to planning (Open questions #9).

Tier dependencies (Tiers 1 and 2 must not depend on any Tier 3 item):

| Tier 3 item | Builds on | Notes |
|---|---|---|
| Platform upgrade (AC-27) | none required | Must keep the API contract (AC-8). Tier 1 and 2 behaviour must still pass afterwards. |
| AngularJS replacement (AC-28) | Tier 1 front-end ACs (AC-14..AC-19) | The same budgets apply to the new front end. Independent of the backend upgrade. |
| Shared cache (AC-29, AC-30) | AC-12, AC-13 | The Tier 1 in-process cache behaviour is what the shared cache replaces. |
| CDN (AC-31, AC-32) | AC-9, AC-10, AC-11 | The CDN relies on the cache headers and Range support defined in Tier 1. |
| Transcoding (AC-33) | AC-20..AC-23 | Runs inside the async upload pipeline from Tier 2. |
| Search index data minimisation (AC-34, AC-35) | AC-25, AC-26 | Existing index must be rebuilt. Avatar computation for search results must keep working. |

## User stories
- US-1: As a visitor, I want the video lists and search results to load quickly so that I can browse without waiting. Covered by: AC-1, AC-2, AC-3, AC-7, AC-12, AC-13
- US-2: As a visitor, I want the home page to load quickly and not download videos I did not play so that it works on a slow connection. Covered by: AC-9, AC-10, AC-14, AC-15, AC-16, AC-17, AC-18, AC-19
- US-3: As a visitor, I want video playback to start and seek without downloading the whole file so that I can watch immediately. Covered by: AC-11, AC-33
- US-4: As the portal owner, I want each viewer counted exactly once per video even under concurrent load, without slowing the video list. Covered by: AC-4, AC-5, AC-6
- US-5: As an uploader, I want the upload request to finish quickly and see my video once it is ready. Covered by: AC-20, AC-21, AC-22, AC-23, AC-24
- US-6: As an operator, I want a fast and avoidable startup reindex. Covered by: AC-25, AC-26
- US-7: As the portal owner, I want existing API clients to keep working. Covered by: AC-8, AC-36
- US-8: As an operator, I want a supported platform and scalable infrastructure. Covered by: AC-27, AC-28, AC-29, AC-30, AC-31, AC-32
- US-9: As a user, I want my email and password hash kept out of the search index. Covered by: AC-34, AC-35

## Acceptance criteria (EARS)
Assumed scale for all numeric checks: 10,000 videos, 1,000 users, dev hardware [ASSUMPTION - see Open questions #10].

### Tier 1 - backend read path
- AC-1: WHEN any of GET /videos, /videos/latest, /videos/popular, /search or a user's video list is requested, the API shall issue at most 3 Mongo queries, independent of the page size (1 up to the maximum).
  Observable: Mongo profiler or a query counter in an integration test shows <= 3 queries, with an equal count for size=1 and size=maximum.
- AC-2: WHEN GET /videos, /videos/latest, /videos/popular or /search is requested at the assumed scale, the API shall respond with p95 <= 300 ms.
  Observable: load-test report (p95 per endpoint) for the full-page requests.
- AC-3: The system shall serve the Latest query (sort by created date), the Popular query (sort by views) and the by-owner query from indexes.
  Observable: Mongo `explain` shows an index scan (no collection scan, no in-memory sort) for each of the three queries.
- AC-4: The system shall resolve the view-statistic lookup by (video id, IP address) with an index.
  Observable: Mongo `explain` shows an index scan for that lookup.
- AC-5: WHEN N view requests for the same video arrive concurrently from N distinct IP addresses, the system shall increase the video's `views` by exactly N.
  Observable: the `views` value after a concurrent test (for example N = 50) equals the start value + N.
- AC-6: WHEN several view requests for the same video arrive concurrently from one IP address, the system shall count exactly one view.
  Observable: `views` increases by 1 and exactly one statistic record exists for that (video, IP).
- AC-7: IF a request's `size` is below 1 or above 50, THEN the API shall use the default size (below 1) or the maximum of 50 (above 50) [ASSUMPTION: maximum 50 - see Open questions #1].
  Observable: the response `size` field and `content` length never exceed 50; the response still has status 200.
- AC-8: The system shall keep the JSON fields and shapes of all existing endpoint responses unchanged, including `owner.avatar` as a Gravatar-or-local URL, `page`/`size` parameters and the `Page` envelope.
  Observable: a contract test comparing field names, types and nesting before and after the change passes for every endpoint touched by this spec.
- AC-9: WHEN a request sends `Accept-Encoding: gzip` for a JSON, HTML, JS or CSS response larger than 1 KB, the system shall return it with `Content-Encoding: gzip`.
  Observable: response header, and a transfer size of at most 40% of the uncompressed size for the JSON list.
- AC-10: WHEN `/static/**`, `/partials/**` or `/media/thumbnails|avatars/**` is requested, the system shall return `Cache-Control` with `max-age` of at least 86400 seconds plus a validator (`ETag` or `Last-Modified`), and shall answer a matching conditional request with 304 [ASSUMPTION: 1 day - see Open questions #3].
  Observable: response headers, and a 304 on a repeat request with `If-None-Match` or `If-Modified-Since`.
- AC-11: WHEN a request for `/media/video/**` carries a valid `Range` header, the system shall respond 206 with `Content-Range` and only the requested bytes; IF the range is not satisfiable, THEN it shall respond 416.
  Observable: status code and `Content-Range` header; body length equals the requested range.
- AC-12: WHILE a cached Latest or Popular result is younger than 30 seconds [ASSUMPTION - see Open questions #2], the API shall answer GET /videos/latest and /videos/popular without any Mongo query.
  Observable: Mongo query counter shows 0 for the second of two requests within 30 s.
- AC-13: WHEN a video is created (processing finished), updated, deleted or its view count changes the Latest/Popular ordering, the system shall evict the cached Latest and Popular results so that the next request reflects the change.
  Observable: after a delete or an update, the next request no longer returns the old data, with no wait for the 30 s TTL.

### Tier 1 - front end
- AC-14: WHEN an anonymous visitor opens the home page with an empty browser cache, the UI shall complete first interaction with at most 15 network requests and at most 1 MB transferred (compressed), excluding video bytes.
  Observable: automated browser network log (count and sum of transfer size).
- AC-15: WHEN a visitor who is not an admin opens any page, the UI shall not request admin-module code.
  Observable: no request for `/static/appjs/admin/**` in the network log for anonymous and non-admin sessions.
- AC-16: The system shall serve JavaScript and CSS libraries (AngularJS, Bootstrap and the app code) in minified form.
  Observable: production responses contain no unminified `angular.js` or `bootstrap.css`; total JS transfer is smaller than the pre-change baseline.
- AC-17: WHILE the user has not pressed play, the UI shall not download video file bytes for any card; only the poster image is requested.
  Observable: no request to `/media/video/**` in the network log before a play action.
- AC-18: WHEN the user presses "Load more", the UI shall request only the next page and shall not re-create the DOM elements of cards already displayed.
  Observable: one list request per click; DOM node identity of existing cards is unchanged (test checks element references).
- AC-19: WHEN a thumbnail or avatar is stored, the system shall store thumbnails at no more than 640 px wide and avatars at no more than 128 px wide, keeping the aspect ratio [ASSUMPTION - see Open questions #4].
  Observable: the stored image dimensions; the average thumbnail size is lower than the baseline.

### Tier 2 - upload and startup
- AC-20: WHEN a user uploads a 100 MB video, the API shall respond in under 5 seconds and shall perform thumbnail creation and search indexing after responding.
  Observable: response time of the upload request; the thumbnail and search document appear afterwards.
- AC-21: WHILE a video's background processing has not finished, the system shall exclude that video from all list and search responses [ASSUMPTION - see Open questions #5].
  Observable: GET /videos, /search and /videos/latest do not contain the video until processing is finished, and contain it afterwards.
- AC-22: IF background processing (copy or thumbnail) fails or is interrupted by an application restart, THEN the system shall remove the partial video record, the stored video file and the temp file, and shall log the failure with the video id.
  Observable: no orphan database record, media file or temp file; one error log line with the video id.
- AC-23: IF search indexing fails after processing, THEN the system shall retry up to 3 times and shall then log the failure; the video shall remain visible in Mongo-backed lists [ASSUMPTION - see Open questions #6].
  Observable: log lines for the retries; the video is present in GET /videos and absent from /search.
- AC-24: WHEN a thumbnail is created, the system shall close every file handle it opened.
  Observable: the process's open file-handle count after 100 uploads equals the baseline plus or minus 5.
- AC-25: WHEN the application starts with a full reindex, the system shall finish indexing 10,000 videos within 60 seconds.
  Observable: startup log timestamps between "Start indexing" and "Finish indexing".
- AC-26: WHEN the application starts and the search index already holds the same number of videos as the video collection [ASSUMPTION - see Open questions #7], the system shall skip the full reindex.
  Observable: no "Clear old index" log line, and search stays available from the first request.

### Tier 3 - platform and infrastructure
- AC-27: The system shall run on a currently supported Java runtime with supported releases of Spring, Spring Security, Spring Data, Jackson and the other libraries in `pom.xml`, with no known critical CVE in the dependency scan.
  Observable: dependency scan report shows zero critical findings; the build and all existing tests pass; AC-8 still passes.
- AC-28: WHERE the AngularJS front end is replaced, the UI shall provide the same features and routes as before and shall still meet AC-14 to AC-19.
  Observable: end-to-end test of the existing user flows passes; AC-14 to AC-19 checks pass on the new front end.
- AC-29: WHERE a shared cache is enabled, the system shall serve the Latest and Popular results from it across all application instances under the same TTL and eviction rules as AC-12 and AC-13.
  Observable: with two instances, a result cached by instance A is served by instance B without a Mongo query; eviction by one is seen by the other.
- AC-30: IF the shared cache is unreachable, THEN the system shall answer from MongoDB without returning an error to the client.
  Observable: with the cache stopped, GET /videos/latest returns 200 and the error is logged.
- AC-31: WHERE a CDN is configured, the system shall serve `/static/**` and `/media/**` URLs from the CDN host, and the CDN shall honour the headers from AC-9, AC-10 and AC-11.
  Observable: URLs in API responses and `index.html` point to the CDN host; Range and conditional requests return 206 and 304 through the CDN.
- AC-32: IF the CDN is unreachable, THEN the system shall remain usable by serving media from the origin.
  Observable: with the CDN host blocked, the home page loads and a video plays through the origin [ASSUMPTION: fallback done at configuration level - see Open questions #9].
- AC-33: WHEN a video upload finishes, the system shall in the background produce a web-optimised rendition (metadata at the start of the file) with a height of at most 720 px, and shall start playback within 3 seconds on a 5 Mbit/s connection for a 100 MB source [ASSUMPTION - see Open questions #8].
  Observable: the stored rendition's container metadata position and height; measured time to first frame.
- AC-34: The system shall store no owner password, recovery code or raw email address in the search index documents.
  Observable: a query of the Elasticsearch document for a video shows none of those fields.
- AC-35: WHEN search results are returned, the API shall still set `owner.avatar` to the same URL form as for other endpoints, without the search document holding the raw email.
  Observable: avatar URLs in /search results are identical to those in GET /videos for the same owner.
- AC-36: The system shall meet AC-1 to AC-26 on the current technology stack without any Tier 3 capability enabled.
  Observable: the Tier 1 and 2 acceptance run passes on a build with every Tier 3 item switched off or not yet delivered.

## Edge cases
- EC-1: Requesting a page beyond the last page, or an empty database, returns an empty page with status 200 and at most 3 queries. Covered by: AC-1
- EC-2: A video whose owner or category no longer exists (dangling reference) must not turn the list into a 500. Accepted: existing behaviour (the aspect catches and logs the error, `GravatarAspect.java:52-54`) is kept; fixing data integrity is out of scope.
- EC-3: Static files change on deploy but browsers hold a one-day copy. Covered by: AC-10 (validators force a conditional request after expiry).
- EC-4: Several requests arrive at the moment the 30 s cache expires, so a few reads hit Mongo together. Accepted: the extra cost is at most a few identical queries every 30 s.
- EC-5: Users behind one proxy or NAT share an IP and count as one viewer. Accepted: the counting rule (one per video per IP) is unchanged.
- EC-6: MongoDB is down while the cache is warm. Accepted: no stale serving after the TTL; the existing error response is kept.
- EC-7: `Range` header is malformed or beyond the file length. Covered by: AC-11
- EC-8: `size` is 0, negative or not a number. Covered by: AC-7
- EC-9: Gravatar is slow or unreachable, so the avatar image fails. Accepted: the existing `checkImage` directive falls back to the local avatar (`app-directives.js:17-26`).
- EC-10: A user uploads a corrupt file that JCodec cannot decode. Covered by: AC-22
- EC-11: The server restarts while a video is processing. Covered by: AC-22
- EC-12: Elasticsearch is unavailable during background indexing. Covered by: AC-23
- EC-13: Searches arrive during a full reindex and may see partial results. Accepted: a full reindex only happens when the index is out of sync (AC-26); a partial result during that window is tolerable.
- EC-14: A user who is also in the middle of changing their avatar. The old file is deleted and a new, resized one is stored. Covered by: AC-19
- EC-15: Existing search documents still contain sensitive fields after deploy. Covered by: AC-34 (the index is rebuilt).
- EC-16: After removing the raw email from search documents, the Gravatar hash cannot be computed for search results. Covered by: AC-35
- EC-17: Shared cache or CDN is unavailable. Covered by: AC-30, AC-32

## Non-functional requirements
- Performance: p95 <= 300 ms for the four list/search endpoints (AC-2); <= 3 Mongo queries per list request (AC-1); upload response < 5 s for 100 MB (AC-20); startup reindex <= 60 s for 10,000 videos (AC-25); home page <= 15 requests and <= 1 MB (AC-14).
- Scale: the targets hold at 10,000 videos and 1,000 users. WHEN load testing, the system shall be exercised with at least 20 concurrent clients and the p95 target shall still hold [ASSUMPTION - see Open questions #10].
- Reliability: the failure behaviour in AC-22, AC-23, AC-30 and AC-32 applies; no request shall return 5xx solely because a cache or CDN is unavailable.
- Compatibility: API contract unchanged (AC-8).
- Security/privacy: no owner password hash, recovery code or raw email in search documents (AC-34).
- Accessibility: not changed by this spec; AC-28 requires no regression against the current UI (the existing UI is the baseline).

## Inputs and provenance
Design sources (supplied by the user): the existing code in /Users/aivlev/Development/Projects/vcp (branch `feat/add-plugin`) and the request "write spec what can be changed for better performance". No Figma, no AGENTS.md/CLAUDE.md, no INSIGHTS.md and no earlier specs exist. The files read:
- `pom.xml`, `src/main/resources/application.properties`
- `config/WebMvcConfig`, `MongoConfig`, `ServiceConfig`, `ElasticSearchConfig`, `ApplicationInitializer`, `SecurityConfig`
- `model/Video`, `User`, `Category`, `VideoStatistic`
- `VideoRepository`, `VideoStatisticRepository`, `UserRepository`
- `VideoServiceImpl`, `UserServiceImpl`, `VideoStatisticServiceImpl`, `VideoProcessorServiceImpl`, `JCodecThumbnailService`, `FileStorageImageService`, `ElasticSearchIndexingService`, `AdminServiceImpl`, `CategoryServiceImpl`
- `VideoController`, `SearchController`, `AdminController`, `VideoStatisticController`
- `GravatarAspect`, `UploadVideoTempStorage`, `VideoStorage`
- `webapp/index.html`, `public/video/list.controller.js`, `list.html`, `details.controller.js`, `details.html`, `app-directives.js`

Runtime data the feature consumes:

| Data | Source |
|---|---|
| Video, User, Category, Company, VideoStatistic documents | MongoDB (db.host/db.port/db.name) |
| Video search documents | embedded Elasticsearch node |
| Video, thumbnail and avatar files | file system under `media.dir` |
| Owner avatars | gravatar.com (third party) |
| `page`, `size`, `query` request parameters, `Range` and `Accept-Encoding` headers | HTTP clients |
| Client IP (`request.getRemoteAddr()`) | HTTP connection |
| Uploaded video and avatar files | authenticated users |
| `index.all.during.startup`, `media.dir`, `base.url` | `application.properties` |

Provenance of requirement areas: findings and evidence come from the existing code; the budgets and the tiering come from the user's answers; the numeric thresholds marked [ASSUMPTION] were inferred by me; the Tier 3 items were added at the user's request.

## Untrusted inputs
- `size`, `page`: WHEN out of range or not numeric, the API shall clamp or reject them without a 5xx (AC-7).
- `query` (search text): the API shall pass it to the search engine only as a query string value, shall cap its length at 200 characters [ASSUMPTION - see Open questions #1], and shall not allow it to change the number of results beyond the page maximum.
- `Range` header: IF malformed or unsatisfiable, THEN the system shall respond 416 or ignore the header, never 5xx (AC-11).
- `Accept-Encoding`, `If-None-Match`, `If-Modified-Since`: the system shall treat them as optional hints and fall back to a full 200 response when invalid.
- Uploaded video and avatar files: the system shall enforce the existing 500 MB limit (`WebMvcConfig.java:37`) and shall treat the content as untrusted during decoding and resizing. IF decoding fails, THEN the upload is rejected and cleaned up (AC-22). Image resizing shall cap the decoded pixel count at 50 megapixels so a small file cannot exhaust memory [ASSUMPTION].
- Client IP: used only as the view-deduplication key; the system shall not trust any client-supplied forwarding header for it unless a trusted proxy is configured [ASSUMPTION].
- Gravatar responses: images loaded from a third party shall fall back to the local avatar on error and shall not block page rendering (EC-9).
- Content returned from the search index and from Mongo (titles, descriptions, names): the UI shall keep rendering it as escaped text.
- Cache content (in-process, shared, CDN): shall be treated as data that may be stale or unavailable (AC-13, AC-30, AC-32).

## Open questions
1. Maximum page size 50 and search query length 200 - default assumed. Confirm or give another value.
2. Latest/Popular cache TTL of 30 seconds - default assumed. Confirm the acceptable staleness.
3. Static/media cache lifetime of 1 day - default assumed.
4. Thumbnail width 640 px and avatar width 128 px - default assumed. Confirm against the real display size and quality needs.
5. Unfinished videos hidden from lists until processing is finished - default assumed. Question: should the uploader see a "processing" state (it needs a new, additive API field)? Suggestion: yes, otherwise a failed upload is invisible to the user.
6. Indexing retries: 3 - default assumed.
7. Skip the full reindex when the index and collection counts match - default assumed. A count match does not prove the content matches; confirm that this is acceptable (Suggestion: also compare a last-updated marker).
8. Transcoded rendition at most 720 px high and playback start within 3 s at 5 Mbit/s - default assumed. Which renditions (resolutions, codecs) and storage budget are acceptable?
9. Tier 3 product choices are open for planning: target Java and Spring versions, the replacement front-end framework, the shared-cache product, the CDN vendor, and how the CDN fallback is done (configuration vs. client logic). Redis, a CDN and transcoding were named as in scope by the user, but no products were chosen.
10. "Dev hardware" is not defined, and the 10,000 videos / 1,000 users scale is an assumption because no other figures were given. Please give a reference machine and the real expected volume and concurrency (20 concurrent clients assumed for load tests).
11. Suggestion (security, needs a decision because it conflicts with "no breaking changes"): the API itself returns `owner.password` (bcrypt hash), `recoveryCode` and `email` in the video list payloads (no `@JsonIgnore` exists in the project). AC-34 covers only the search index. Hiding them from the API also shrinks the payload. This is not in the ACs as it changes the response shape; decide whether to add it.
12. Suggestion: the view-tracking directive uses `videos[v.id.viewed]` (`app-directives.js:59-66`), which is `undefined` for every video, so once one video qualifies no other video on the page is counted. A correctness fix next to AC-5 and AC-6.
13. Suggestion: the home page can show the same video in Latest, Popular and Videos at once; excluding duplicates would cut DOM size.
14. Suggestion: cap the number of rendered cards (list virtualization or a "load more" window) to keep the DOM bounded in long sessions.
15. Suggestion: the admin statistics make 4 separate count queries (`AdminServiceImpl.java:42-49`); fine today, cache them if the admin page gets slow.
16. Suggestion: the `Page` envelope runs a count query per request (`VideoController.java:32-36`); a "has next" style response would remove it but changes the contract, so it is left out of the ACs.
