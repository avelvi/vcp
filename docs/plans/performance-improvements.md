# Development Plan: Performance improvements (read path, front-end load, view counting, async upload, platform)

## Goal
Deliver the tiered performance work from the spec: Tier 1 (backend read path and front-end load), Tier 2 (async upload and fast/avoidable reindex), Tier 3 (platform, cache, CDN, transcoding, search-index data minimisation). Tiers 1 and 2 must work on the current stack without any Tier 3 item (AC-36), and the HTTP/JSON contract must not change (AC-8).

## Spec
`/Users/aivlev/Development/Projects/vcp/specs/2026-10-18-performance-improvements.md`, SPEC-2026-10-18-performance-improvements, Status: **draft** (not `approved`; the caller asked to plan anyway, so this plan proceeds on the draft. Re-check the plan if the spec changes on approval).

## Execution mode
Single-agent - default, because the planning run could not ask the user. One agent works through the work items in the order below on branch `feat/add-plugin` in the current checkout, running the check commands after each item. If the user later switches to multi-agent mode, see "Multi-agent notes" at the end of the Work items section.

## Requirements review
- Requirements as understood: AC-1..AC-36 of the spec, unchanged (see Traceability).
- Clarifications resolved (from the user, via the caller): tiered scope (Tier 1 then 2 then 3); no breaking change to the HTTP/JSON contract; budgets as written in the spec.
- Still open (not decided, planned as pending, not committed):
  - Spec Open question #11 (stop returning owner.password / recoveryCode / email in the API): see optional item OPT-A. It is NOT part of the committed items.
  - Tier 3 product choices (Java/Spring target, front-end framework, cache product, CDN vendor, transcoding tool): prerequisites of WI-19; Tier 3 items are planned coarsely.
  - Spec assumptions #1-#10 (max page 50, TTL 30 s, 1-day cache, 640/128 px, hide unfinished videos, 3 retries, count-match reindex skip, 720 px rendition, scale/hardware): this plan implements the defaults; each value must live in one named constant or property so it can change without code rework.
- Recommendations (advice; the user decides):
  1. Spec problems to settle before or during implementation (details under Risks): AC-1 is not reachable with the current data model without a decision (WI-5), AC-14 is not reachable with 18 poster images plus avatars unless images are lazy-loaded and "first interaction" is defined (WI-13), AC-34/35 conflict with AC-8 for /search unless search hits are hydrated from Mongo (WI-20), and AC-13 conflicts with the 30 s cache if every view evicts it (WI-4/WI-6). -> accepted as designed in the items below; trade-off: slightly more code than a literal reading of the spec.
  2. Track an upload in a separate `UploadJob` collection and insert the `Video` document only when processing is complete (WI-15), instead of adding a `processing` flag to `Video`. -> why: lists, indexes (AC-3), search and counts stay untouched, AC-21 is satisfied by construction, restart recovery (AC-22) is a scan of the jobs collection, no response-shape change. Trade-off: one more small collection. Accepted as the plan default.
  3. Do open question #12 (the `videos[v.id.viewed]` bug in `app-directives.js:59-66`) as OPT-B next to WI-4, because it directly affects whether AC-5/AC-6 views are ever sent. -> deferred, needs the user's go-ahead (it is not in any AC).
  4. Open questions #13-#16: deferred, not planned (#16 changes the contract).
- Out of scope: writing or editing the spec; changing JSON field names/shapes; changing the 500 MB upload limit; visual redesign; migrating already-stored full-size thumbnails/avatars (only newly stored images are resized, AC-19 says "when stored"); fixing dangling references (EC-2); `Page` count elision (Open question #16).

## Shared context (every implementer reads this instead of re-deriving it)
- Layout: single Maven module `vcp` (WAR), package `com.aivlev.vcp` with `config`, `controller`, `service`, `service/impl`, `repository/storage` (Mongo), `repository/search` (Elasticsearch), `aop`, `model`, `dto`, `security`, `exception`. Front end: `src/main/webapp` (`index.html`, `static/appjs/**`, `static/js`, `static/css`, `partials`). Tests: `src/test/java` (JUnit 4.11, Mockito 1.10 `mockito-all`, spring-test, embedded Mongo via `de.flapdoodle.embed.mongo`; configs in `src/test/java/com/aivlev/vcp/config`).
- Stack constraints: `maven-compiler-plugin` source/target **1.7** (no lambdas, no streams, no `java.time`, no Caffeine; Guava or hand-written code is fine). Spring 4.2.5, Spring Security 4.0.4, Spring Data MongoDB 1.9.1, Spring Data Elasticsearch 2.0.1 (embedded node), Jackson 2.7.3, Servlet 3.1. Java config only (`ApplicationInitializer`, `WebMvcConfig`, `ServiceConfig`, ...).
- Layering (onion): controller (thin) -> service -> repository -> model. Mongo/ES access only in repositories or a repository-level helper (e.g. custom repository impl using `MongoTemplate`); the `GravatarAspect` runs `@AfterReturning` on `VideoService` methods returning `Page` and on `UserService.findVideos`.
- Check commands: the project documents none (no README/AGENTS.md). Assume `mvn test` (unit + embedded-Mongo integration tests; first run downloads a Mongo binary, needs network) and `mvn package` (also runs the antrun copy). `mvn` was not on PATH on the planning machine and the local JDK is 11 while the target is 1.7: the implementer must confirm the working command and record it here in the plan's Risks section if it differs.
- Do not hand-edit: `src/main/webapp/WEB-INF/lib` (generated by the antrun `copy-lib` step on `package`), `target/`, and vendored libraries `src/main/webapp/static/js/angular*.js`, `static/appjs/ui-router.js`, `static/appjs/ng-file-upload.js`, `static/css/bootstrap*.css` (replace them through the build step of WI-11, do not edit their contents).
- Contract guard: WI-1 produces the contract snapshot. Every later item re-runs it. No endpoint field may be added, removed or renamed (additive fields only if a later decision says so).
- Tunables (single source each, property or constant): max page size 50, default size, search query max length 200, list cache TTL 30 s, static cache max-age 86400, thumbnail max width 640, avatar max width 128, image max pixels 50,000,000, index retries 3.
- Order (single-agent): WI-1 -> WI-2 -> WI-3 -> WI-4 -> WI-5 -> WI-6 -> WI-7 -> WI-8 -> WI-9 -> WI-10 -> WI-11 -> WI-12 -> WI-13 (Tier 1 complete, checkpoint CP-1) -> WI-14 -> WI-15 -> WI-16 -> WI-17 (Tier 2 complete, checkpoint CP-2, which is the AC-36 run) -> Tier 3 (WI-19 ... WI-25).
- Checkpoint after every item: compile + the relevant tests + the contract snapshot (WI-1).

## Traceability (spec -> work items)
| Spec criterion | Covered by | Notes |
|---|---|---|
| AC-1 | WI-5 (also WI-1 query counter) | needs a decision on categories/companies, see Risks R-1 |
| AC-2 | WI-2, WI-5, WI-6 (verified by WI-1 load test) | |
| AC-3 | WI-2 | |
| AC-4 | WI-2 | unique (videoId, ipAddress) index |
| AC-5 | WI-4 | |
| AC-6 | WI-2, WI-4 | |
| AC-7 | WI-3 | also search query cap |
| AC-8 | WI-1 (guard), every item | OPT-A would break it unless the user decides |
| AC-9 | WI-7 | |
| AC-10 | WI-8 | |
| AC-11 | WI-9 | |
| AC-12 | WI-6 | |
| AC-13 | WI-6, WI-4 (view eviction hook) | see R-3 |
| AC-14 | WI-11, WI-12, WI-13 | needs a definition of "first interaction", R-2 |
| AC-15 | WI-12 | |
| AC-16 | WI-11 | |
| AC-17 | WI-13 | |
| AC-18 | WI-13 | |
| AC-19 | WI-10 | new images only |
| AC-20 | WI-15 | |
| AC-21 | WI-15 | by construction (UploadJob) |
| AC-22 | WI-15 | |
| AC-23 | WI-16 | |
| AC-24 | WI-14 | |
| AC-25 | WI-17 | |
| AC-26 | WI-17 | |
| AC-27 | WI-24 | Tier 3 |
| AC-28 | WI-25 | Tier 3 |
| AC-29 | WI-21 | Tier 3 |
| AC-30 | WI-21 | Tier 3 |
| AC-31 | WI-22 | Tier 3 |
| AC-32 | WI-22 | Tier 3 |
| AC-33 | WI-23 | Tier 3 |
| AC-34 | WI-20 | Tier 3 |
| AC-35 | WI-20 | Tier 3 |
| AC-36 | CP-1, CP-2 (WI-1 suite run with Tier 3 off) and the Tier 3 rules below | |

## Affected modules
- vcp (the only Maven module) - backend (config, controller, service, repository, aop, model) and front end (`src/main/webapp`).

## Work items

Tier 1 = WI-1..WI-13 (13 items). Tier 2 = WI-14..WI-17 (4 items). Tier 3 = WI-19..WI-25 (7 items; WI-18 is intentionally unused so Tier 3 numbering starts at the decision record). Optional/pending, not counted: OPT-A, OPT-B, OPT-C.

### Tier 1 - backend read path

### WI-1: Baseline and verification harness
- Module: vcp
- Stack: backend
- Depends on: none
- Parallel-safe with: none - first item
- Chain: WI-1 -> WI-2 (same context is fine)
- Covers: AC-8, supports AC-1, AC-2, AC-14, AC-36
- Skills to use: engineering-paved-path:onion-architecture
- Description: Build the measurement and guard tooling the other items rely on. (a) A contract snapshot test: for GET /videos, /videos/latest, /videos/popular, /videos/{id}, /search, the user video list and the other endpoints touched by this spec, record the JSON field names, types and nesting (golden file under `src/test/resources`) and assert it. (b) A Mongo query counter usable in integration tests (e.g. enable the profiler on the embedded Mongo or wrap `MongoTemplate`/`MongoDbFactory`) that returns the number of queries issued during a call. (c) A seed helper that creates 10,000 videos and 1,000 users (reusing `TestDataService` ideas where possible). (d) A repeatable load script (documented command, 20 concurrent clients) reporting p95 per list endpoint, kept outside the production code (e.g. `src/dev` or `docs/perf/`). (e) A baseline record (a short markdown table in `docs/perf/baseline.md`) of current p95, query counts, home-page request count/bytes, startup reindex time, upload time, so later items can show improvement. No production behaviour changes.
- Files likely touched: `src/test/java/com/aivlev/vcp/**` (new support classes and a contract test), `src/test/resources/contract/**`, `docs/perf/**`, optionally `src/dev/**`
- Acceptance criteria:
  - The contract test passes on the unchanged code and fails if a field is renamed or removed.
  - The query counter reports a number for a page request in an integration test.
  - The baseline document has a value for each metric listed above, or states why it could not be measured.

### WI-2: Mongo indexes for Video and VideoStatistic
- Module: vcp
- Stack: backend
- Depends on: WI-1
- Parallel-safe with: none - single-agent order (in multi-agent mode it only touches `model/Video.java`, `model/VideoStatistic.java`, `config/MongoConfig.java`)
- Chain: none
- Covers: AC-3, AC-4, AC-6 (uniqueness part), supports AC-2
- Skills to use: engineering-paved-path:onion-architecture
- Description: Add indexes (annotations on the model or a `MongoConfig` index initialiser; follow how `@Indexed` is already used on User/Category): `Video.createdDate` (field `created_date`, descending), `Video.views` (descending), `Video.owner` (the DBRef id, so `findByOwnerId` is served by an index; verify the generated query is on `owner.$id` and index that path), and a compound unique index on `VideoStatistic(videoId, ipAddress)`. Note `createdDate` is a String of epoch milliseconds; it sorts correctly while all values have 13 digits (true until the year 2286), so keep it as a String (no contract change) and document that in a comment. Existing data may already contain duplicate (videoId, ipAddress) rows: the unique index would fail to build, so add a one-off, idempotent dedupe step run before the index is ensured (log how many rows were removed), or make it a documented operator step if the user prefers; do not edit data silently.
- Files likely touched: `src/main/java/com/aivlev/vcp/model/Video.java`, `model/VideoStatistic.java`, `config/MongoConfig.java`, `src/test/java/**` (index tests)
- Acceptance criteria:
  - An integration test runs `explain` for the Latest, Popular and by-owner queries and sees an index scan and no in-memory sort (AC-3).
  - `explain` for `findByVideoIdAndIpAddress` shows an index scan (AC-4).
  - Inserting two statistic rows with the same (videoId, ipAddress) fails with a duplicate-key error (AC-6).
  - The contract snapshot is unchanged.

### WI-3: Page size bounds and search query length cap
- Module: vcp
- Stack: backend
- Depends on: WI-1
- Parallel-safe with: none - touches `WebMvcConfig` which WI-7/WI-8 also touch
- Chain: none
- Covers: AC-7, EC-8
- Skills to use: engineering-paved-path:onion-architecture, engineering-paved-path:security
- Description: Configure the Spring Data `PageableHandlerMethodArgumentResolver` (via `WebMvcConfigurerAdapter.addArgumentResolvers` in `WebMvcConfig`, replacing what `@EnableSpringDataWebSupport` registers if needed) with max page size 50, and make size < 1 or not numeric fall back to the controller's `@PageableDefault`. A negative or non-numeric `page` must resolve to page 0, never a 5xx. Cap the `query` parameter of `/search` at 200 characters (truncate; do not reject, to keep the contract) in `SearchController` or the service. Keep the `Page` envelope and `page`/`size` names.
- Files likely touched: `src/main/java/com/aivlev/vcp/config/WebMvcConfig.java`, `controller/SearchController.java`, `controller/VideoController.java` (only if defaults need to be explicit), tests
- Acceptance criteria:
  - `size=500` returns status 200 with `size` and content length at most 50; `size=0`, `size=-3`, `size=abc` return 200 with the default size.
  - A 5,000-character `query` returns 200 and the search receives at most 200 characters.
  - The contract snapshot is unchanged.

### WI-4: Atomic, cheap view counting
- Module: vcp
- Stack: backend
- Depends on: WI-2
- Parallel-safe with: none - touches `VideoStatisticServiceImpl`, repositories
- Chain: WI-4 -> WI-5 (both rewrite the read/write path of `Video`)
- Covers: AC-5, AC-6, AC-13 (hook), EC-5
- Skills to use: engineering-paved-path:onion-architecture, engineering-paved-path:security
- Description: Replace the read-modify-save in `VideoStatisticServiceImpl.updateVideoViews` with: (1) an atomic upsert/insert of the `VideoStatistic(videoId, ipAddress)` that relies on the unique index and treats a duplicate-key outcome as "already counted"; (2) only when a new statistic row was really inserted, an atomic `$inc views` on the `Video` document (`MongoTemplate.updateFirst` with `Update.inc`, in a custom repository fragment so it stays in the repository layer); no whole-document save. Check the video exists first with a cheap existence query, not by loading the document. Keep the one-view-per-video-per-IP rule and `request.getRemoteAddr()` as the key (do not trust forwarding headers). After a successful increment, call a `ListCacheEviction` hook (interface defined here as a no-op bean, implemented in WI-6) with the video id and new view count; WI-6 decides whether Popular must be evicted (only when the video is in, or now belongs in, the cached Popular page; evicting on every view would make AC-12 unusable, see R-3). Do not touch the Elasticsearch copy (no AC requires it; mention the staleness in a code comment).
- Files likely touched: `service/impl/VideoStatisticServiceImpl.java`, `repository/storage/VideoRepository.java` (+ custom impl), `repository/storage/VideoStatisticRepository.java`, `service/VideoStatisticService.java` only if signatures need to change, `controller/VideoStatisticController.java` (read only unless needed), tests
- Acceptance criteria:
  - 50 concurrent calls from 50 distinct IPs raise `views` by exactly 50 (AC-5).
  - Many concurrent calls from one IP raise `views` by 1 and leave exactly one statistic row (AC-6).
  - Unknown video id: no statistic row created, no 5xx (keep current behaviour).
  - Existing unit test `VideoStatisticServiceImplTest` is updated to the new collaborators and passes.

### WI-5: Remove the N+1 on list endpoints
- Module: vcp
- Stack: backend
- Depends on: WI-1, WI-2 (WI-4 in single-agent order)
- Parallel-safe with: none - touches `Video` loading and `GravatarAspect`
- Chain: none
- Covers: AC-1, AC-2, EC-1, EC-2, AC-8 (guard)
- Skills to use: engineering-paved-path:onion-architecture
- Description: Make GET /videos, /videos/latest, /videos/popular, /search and the user video list issue at most 3 Mongo queries regardless of page size. Current cost per page: 1 page query + 1 count + lazy loads of owner, category, and the owner's company (and possibly owner authorities) per video. Design (decision R-1): keep the entities and JSON unchanged; after the page query, resolve all owner ids in ONE `$in` query (sets the owner users, including their company ref resolved from a small in-process reference cache), and resolve categories and companies from an in-process cache of those two tiny collections (loaded once, evicted on any write through `CategoryService` / `CompanyService`). That yields page + count + owners = 3 queries. Implement as a repository-level helper (e.g. `VideoReferenceResolver`) called by the service layer so the lazy proxies are never touched during serialization. Make sure the owner's `authorities` lazy list does not trigger per-owner queries during JSON serialization (verify with the query counter; if it does, resolve them in the same `$in` pass or exclude them from the response only if the contract snapshot shows they are not serialized today). Keep `GravatarAspect` behaviour, but make `rewriteAvatar` idempotent (skip if the avatar already starts with the gravatar prefix) because WI-6 will return cached objects through the aspect. Keep the existing "log and continue" behaviour for dangling references (EC-2). Alternative if the user rejects the in-process reference cache: embed a denormalised owner/category summary in `Video` (breaks nothing in JSON but needs a data migration) - not planned.
- Files likely touched: `service/impl/VideoServiceImpl.java`, `service/impl/UserServiceImpl.java` (`findVideos`), `repository/storage/**` (new helper), `service/impl/CategoryServiceImpl.java`, `service/impl/CompanyServiceImpl.java` (cache eviction), `aop/GravatarAspect.java`, tests
- Acceptance criteria:
  - With the WI-1 query counter, each of the five endpoints issues <= 3 Mongo queries for size=1 and the same number for size=50 (AC-1).
  - Empty database and page beyond the last page return 200 with an empty page and <= 3 queries (EC-1).
  - A video with a deleted category or owner still returns 200 for the list (EC-2).
  - Updating a category name is visible on the next list request (cache eviction works).
  - Contract snapshot unchanged; calling the aspect twice on the same user does not double-wrap the avatar URL.
  - Load test at 10,000 videos, 20 clients: p95 <= 300 ms for /videos, /videos/latest, /videos/popular, /search (AC-2; also depends on WI-2 and WI-6).

### WI-6: In-process cache for Latest and Popular
- Module: vcp
- Stack: backend
- Depends on: WI-4, WI-5
- Parallel-safe with: none - touches `VideoServiceImpl`, `ServiceConfig`
- Chain: none
- Covers: AC-12, AC-13
- Skills to use: engineering-paved-path:onion-architecture
- Description: Enable Spring caching (`@EnableCaching` in `ServiceConfig` or a new `CacheConfig`) with an in-process cache that has a 30 s write-expiry. Spring 4.2's `ConcurrentMapCache` has no TTL, so use a Guava `CacheBuilder` (Java 7 compatible) wrapped in a Spring `Cache`/`CacheManager`; add the Guava dependency in `pom.xml` through the normal dependency declaration. Cache `findTop3ByOrderByCreatedDateDesc` and `findTop3ByOrderByViewsDesc` results keyed by (cache name, page, size). Keep the cache behind the Spring `CacheManager` abstraction and the `ListCacheEviction` interface from WI-4 so that WI-21 can swap in a shared cache without touching callers (Tier 3 builds on this, not the reverse). Evict both caches on video create (when WI-15 inserts the Video), update, delete, and on view changes per the rule in WI-4 (evict Popular only when the incremented video is in the cached page or its new count exceeds the lowest count in it). Decide and document the interplay with `GravatarAspect`: cached values must be the already-rewritten result or the rewrite must be idempotent (WI-5 makes it idempotent), and cached pages must not be mutated by callers.
- Files likely touched: `pom.xml` (Guava only), `config/ServiceConfig.java` or new `config/CacheConfig.java`, `service/impl/VideoServiceImpl.java`, `service/impl/VideoStatisticServiceImpl.java` (hook impl wiring), tests
- Acceptance criteria:
  - Two requests to /videos/latest within 30 s: 0 Mongo queries for the second (AC-12); same for /videos/popular.
  - After delete or update of a video, the next request returns fresh data without waiting 30 s (AC-13).
  - After 30 s the cache refreshes from Mongo.
  - A single view that does not change the Popular page does not evict it.
  - Contract snapshot unchanged.

### Tier 1 - HTTP delivery

### WI-7: Response compression
- Module: vcp
- Stack: backend
- Depends on: WI-1
- Parallel-safe with: none (touches `ApplicationInitializer`)
- Chain: WI-7 -> WI-8 -> WI-9 (all HTTP delivery, same files)
- Covers: AC-9
- Skills to use: engineering-paved-path:security
- Description: Add a gzip servlet filter registered in `ApplicationInitializer` (the container's own compression is deployment configuration and cannot be guaranteed from the WAR). Compress responses whose content type is JSON, HTML, JS or CSS and whose size exceeds 1 KB, only when the request has `Accept-Encoding: gzip`; set `Vary: Accept-Encoding`; never compress `/media/**` binaries (video, jpg). Write the filter in-house or use a small maintained library; confirm the choice builds on Java 7. It must wrap the output stream correctly with HEAD requests, 304 and error responses.
- Files likely touched: `config/ApplicationInitializer.java`, new `src/main/java/com/aivlev/vcp/web/GzipFilter.java` (or similar), `pom.xml` only if a library is used, tests
- Acceptance criteria:
  - A request with `Accept-Encoding: gzip` for the 12-item /videos list returns `Content-Encoding: gzip` and a body <= 40% of the uncompressed size (AC-9).
  - Without the header the response is unchanged; `/media/video/**` is never compressed; responses under 1 KB are not compressed.

### WI-8: Cache headers and conditional requests for static content
- Module: vcp
- Stack: backend
- Depends on: WI-7
- Parallel-safe with: none (same file `WebMvcConfig`)
- Chain: WI-7 -> WI-8 -> WI-9
- Covers: AC-10, EC-3
- Skills to use: none
- Description: In `WebMvcConfig.addResourceHandlers` set `setCachePeriod(86400)` for `/static/**`, `/partials/**`, `/media/thumbnails/**` and `/media/avatars/**`, keeping `Last-Modified` handling (Spring 4.2's resource handler answers `If-Modified-Since` with 304). Add an `ETag` only if cheap (do not use `ShallowEtagHeaderFilter` on `/media/video/**` or on streamed content). `index.html` gets a short or `no-cache` policy with validators so deployments are picked up (EC-3). Separate the handler for `/media/video/**` now (it is replaced in WI-9); video responses must not be forced to max-age beyond what AC-10 lists.
- Files likely touched: `config/WebMvcConfig.java`, tests
- Acceptance criteria:
  - Responses for the listed paths carry `Cache-Control: max-age=86400` (or larger) and `Last-Modified`/`ETag`.
  - A repeat request with `If-Modified-Since`/`If-None-Match` returns 304 with an empty body.
  - `index.html` does not carry a one-day max-age.

### WI-9: HTTP Range support for video files
- Module: vcp
- Stack: backend
- Depends on: WI-8
- Parallel-safe with: none (same file `WebMvcConfig`)
- Chain: WI-7 -> WI-8 -> WI-9
- Covers: AC-11, EC-7
- Skills to use: engineering-paved-path:security
- Description: Spring 4.2.5's `ResourceHttpRequestHandler` has no reliable Range support (confirm with a test first; native Range support arrived in Spring 4.3 / `ResourceRegion`). Implement a custom handler/controller for `/media/video/**` that resolves the file under the configured media location, rejects path traversal (`..`, encoded variants), sets `Accept-Ranges: bytes`, answers a valid single range with 206, `Content-Range` and exactly the requested bytes, answers an unsatisfiable range with 416 and `Content-Range: bytes */<size>`, and ignores or rejects (416) a malformed header, never a 5xx. Stream with a bounded buffer (no loading the file into memory). Keep serving the full file with 200 when there is no Range header. Support `If-Range` only if trivial; multi-range requests may fall back to a full 200.
- Files likely touched: `config/WebMvcConfig.java`, new `src/main/java/com/aivlev/vcp/web/VideoRangeResourceHandler.java` (or a controller), tests
- Acceptance criteria:
  - `Range: bytes=0-99` returns 206, `Content-Range: bytes 0-99/<size>`, body length 100 (AC-11).
  - `Range: bytes=999999999-` on a small file returns 416; `Range: bytes=abc` returns 416 or the full 200, never 5xx (EC-7).
  - `GET /media/video/../../etc/passwd` (and encoded forms) returns 400/404, not file content.

### WI-10: Resized thumbnails and avatars
- Module: vcp
- Stack: backend
- Depends on: WI-1
- Parallel-safe with: none (touches `JCodecThumbnailService`, which WI-14 also changes)
- Chain: WI-10 -> WI-14 (same file)
- Covers: AC-19, EC-14
- Skills to use: engineering-paved-path:security
- Description: Scale newly stored thumbnails to at most 640 px wide and avatars to at most 128 px wide keeping the aspect ratio (no upscaling), using `javax.imageio`/`java.awt` (no new dependency). Apply in `JCodecThumbnailService` (after the frame is decoded) and in `FileStorageImageService` / `UserServiceImpl` avatar save path. Before decoding an uploaded avatar, read the image dimensions through an `ImageReader` and reject (existing `BadImageException`) anything over 50 megapixels. Keep the output format and URL scheme (`/media/thumbnails/...`, `/media/avatars/...`) unchanged. On avatar change, delete the old file and store the new resized one (EC-14). Existing stored images are not touched.
- Files likely touched: `service/impl/JCodecThumbnailService.java`, `service/impl/FileStorageImageService.java`, `service/impl/UserServiceImpl.java`, `service/ImageService.java` (only if needed), tests (`UserServiceImplTest`, `VideoProcessorServiceImplTest`)
- Acceptance criteria:
  - A 1920x1080 frame yields a stored thumbnail 640x360; a 100 px wide image is not enlarged.
  - A 1000x1000 avatar is stored as 128x128; aspect ratio is kept for non-square input.
  - An image above 50 megapixels is rejected without an OutOfMemoryError.
  - Average new thumbnail size is lower than the WI-1 baseline.

### Tier 1 - front end

### WI-11: Front-end build, minification and bundling
- Module: vcp
- Stack: ui
- Depends on: WI-1
- Parallel-safe with: none (rewrites `index.html`, `pom.xml` build section)
- Chain: WI-11 -> WI-12 -> WI-13
- Covers: AC-16, AC-14 (request count part)
- Skills to use: none (the project has no front-end skill; `engineering-paved-path:frontend-architecture` and `react-best-practices` are for React/Next.js and do not apply to AngularJS)
- Description: The repo has no JS build. Add a Maven-driven step that runs at `package` (candidates: `com.samaxes.maven:minify-maven-plugin` or `frontend-maven-plugin`; pick one that works with JDK 7/8 and does not require a global Node install, record the choice) to concatenate and minify app code into a few bundles (e.g. `vendor.min.js`, `app.min.js`), and to use minified AngularJS and Bootstrap (vendored `*.min.*` files added via the build or committed as new files; do not edit the existing vendored files). The controllers use array-style DI (`['$scope', ..., function(...)]`), so mangling is safe; verify every controller/service/directive/config block, since a single implicit-annotation function breaks under mangling. Host `ui-bootstrap-tpls` locally instead of the cloudflare CDN so Tier 1 does not depend on a third party and the bundle can be cached. Inline the navbar template (`public/navbar/havbar.html`) into a `$templateCache` entry in the bundle so the separate ng-include request disappears. Keep an unminified dev mode (Maven profile or property) so developers can debug. `index.html` references bundles in production and individual files in dev; the switch must be a build-time substitution, not duplicated HTML. Source maps are optional.
- Files likely touched: `pom.xml` (build plugin section only), `src/main/webapp/index.html`, `src/main/webapp/static/appjs/public/navbar/**`, new `src/main/webapp/static/js/*.min.js`, `static/css/*.min.css` (generated or new files), docs note
- Acceptance criteria:
  - The production WAR serves no unminified `angular.js` or `bootstrap.css`; total JS transfer is below the WI-1 baseline (AC-16).
  - Home page loads and every public route works with the bundles (manual smoke or browser test).
  - The home page no longer requests `havbar.html` separately.
  - A dev build still works unminified.

### WI-12: Load admin code only for admins
- Module: vcp
- Stack: ui
- Depends on: WI-11
- Parallel-safe with: none (same files as WI-11/WI-13)
- Chain: WI-11 -> WI-12 -> WI-13
- Covers: AC-15, AC-14
- Skills to use: none (AngularJS 1.x, see WI-11)
- Description: Remove the five admin module groups (`static/appjs/admin/{category,company,home,users,video}`) from the main bundle and publish them as a separate `admin.min.js`. Load it on demand only after the authenticated user is known to be an admin (the role is available from the existing auth service/`UserDetails`), and register the admin ui-router states lazily (keep a reference to `$stateProvider`/`$urlRouterProvider` from a config block, or use a small lazy-loading mechanism; ui-router here is a vendored 0.x copy). Deep links to admin URLs must still work after login: on an admin URL, load the bundle first, then resolve the state. Non-admin and anonymous users must not request admin code and keep getting the existing access-denied behaviour when visiting admin URLs. The server-side authorization (`@PreAuthorize`) is unchanged and remains the real protection.
- Files likely touched: `src/main/webapp/index.html`, `static/appjs/app.js`, `static/appjs/admin/**/config.js`, `static/appjs/public/auth/**`, `pom.xml` build config from WI-11
- Acceptance criteria:
  - Network log for anonymous and non-admin sessions contains no request for `/static/appjs/admin/**` or `admin.min.js` (AC-15).
  - An admin logging in gets the admin pages working, including a hard refresh on an admin URL.
  - The public home page request count is lower than after WI-11.

### WI-13: Light home page and Load more
- Module: vcp
- Stack: ui
- Depends on: WI-11, WI-12
- Parallel-safe with: none
- Chain: WI-11 -> WI-12 -> WI-13
- Covers: AC-17, AC-18, AC-14
- Skills to use: none (AngularJS 1.x, see WI-11)
- Description: In `public/video/list.html` and `list.controller.js`: add `preload="none"` to every `<video>` and keep the `poster` attribute so only the poster is requested before play (AC-17; check that `ng-src` on `<video>` does not trigger a download). Lazy-load below-the-fold poster and avatar images (native `loading="lazy"` where supported plus a small fallback directive; see R-2 for the budget arithmetic). Fix "Load more" so that it requests only the next page and appends to the existing array without replacing existing items; use `track by video.id` on the three `ng-repeat`s so existing DOM nodes keep identity (AC-18). Do not change the three API calls' URLs or parameters. Keep the existing `checkImage` avatar fallback. No visual redesign. Related spec suggestions #12-#14 are NOT done here (see OPT-B and the deferred list).
- Files likely touched: `static/appjs/public/video/list.html`, `static/appjs/public/video/list.controller.js`, `static/appjs/app-directives.js` (lazy image directive only), `static/css/app.css` only if needed for placeholders
- Acceptance criteria:
  - Before pressing play, no request to `/media/video/**` appears in the network log (AC-17).
  - One "Load more" click sends exactly one list request with the next page, and the element references of previously rendered cards are unchanged (AC-18).
  - Home page with an empty cache: <= 15 requests and <= 1 MB transferred (compressed) up to first interaction, with "first interaction" defined as in R-2 (AC-14); measured with the harness from WI-1.

### CP-1: Tier 1 checkpoint (not a work item)
Run the WI-1 suite: contract snapshot, query counts, load-test p95, home-page measurement. Record numbers in `docs/perf/results.md`. Tier 1 done when AC-1..AC-19 pass.

### Tier 2 - upload and startup

### WI-14: Close file handles in thumbnail creation
- Module: vcp
- Stack: backend
- Depends on: WI-10
- Parallel-safe with: none (same file as WI-10)
- Chain: WI-10 -> WI-14
- Covers: AC-24
- Skills to use: none
- Description: Tiny mechanical item. In `JCodecThumbnailService` (around line 54) use try/finally (Java 7 try-with-resources is allowed) so the `FileChannel`/`NIOUtils` source and any streams are always closed, including on decode failure. No behaviour change otherwise. Can be done inline by the caller or a cheaper model.
- Files likely touched: `service/impl/JCodecThumbnailService.java`, test
- Acceptance criteria:
  - After 100 thumbnail creations in a test, the process open-handle count is baseline +/- 5 (AC-24), also when the input is a corrupt file.

### WI-15: Asynchronous upload pipeline with UploadJob
- Module: vcp
- Stack: backend
- Depends on: WI-6, WI-10, WI-14
- Parallel-safe with: none (rewrites `UploadVideoTempStorage`, `VideoProcessorServiceImpl`, `UserServiceImpl.uploadVideo`)
- Chain: WI-15 -> WI-16
- Covers: AC-20, AC-21, AC-22, EC-10, EC-11
- Skills to use: engineering-paved-path:onion-architecture, engineering-paved-path:security
- Description: Make the upload request do only the minimum: validate the form, move the multipart upload to a temp file (rename/move when on the same filesystem so a 100 MB file does not take seconds), create an `UploadJob` document (id, owner login, category id, title/description, temp path, state PENDING/FAILED, created time) and return the current success response. Then a `ThreadPoolTaskExecutor` (`@EnableAsync` is available in Spring 4.2; bounded pool and queue, configured in `ServiceConfig`) runs: copy to `media.dir/video`, decode a frame and create the (resized) thumbnail, insert the `Video` document, index it in Elasticsearch (retries from WI-16), evict the list caches (WI-6 hook), delete the temp file and the job. Because the `Video` document is inserted only at the end, unfinished videos are never in lists or search (AC-21) and no list query or index changes. The existing `UploadVideoTempStorage` aspect's thread-local temp path does not survive a thread hop: replace it with explicit passing of the path. On any failure (copy, decode, insert), delete the copied video file, thumbnails and temp file, mark or remove the job, and log ONE error line containing the video/job id (AC-22). On application start, a component scans `UploadJob` rows left in PENDING state, performs the same cleanup and logs each (restart case). Security: temp file names are server-generated; do not trust client file names. Keep the 500 MB limit. Update `VideoProcessorServiceImplTest`/`UserServiceImplTest` for the new collaborators.
- Files likely touched: `aop/UploadVideoTempStorage.java`, `service/impl/VideoProcessorServiceImpl.java`, `service/impl/UserServiceImpl.java`, `service/VideoProcessorService.java`, new `model/UploadJob.java`, new `repository/storage/UploadJobRepository.java`, new `service/impl/UploadVideoTask.java` (async worker), `config/ServiceConfig.java`, `controller/UserController.java` (only if the response must change; it must not), tests
- Acceptance criteria:
  - Uploading a 100 MB file returns in < 5 s (AC-20), measured with the WI-1 harness.
  - During processing the video is absent from GET /videos, /videos/latest, /search; afterwards it is present (AC-21).
  - A corrupt file and a simulated restart each leave no `Video` row, no media file, no temp file, and exactly one error log line with the id (AC-22).
  - The upload endpoint response shape is unchanged (contract snapshot).

### WI-16: Search indexing retries
- Module: vcp
- Stack: backend
- Depends on: WI-15
- Parallel-safe with: none
- Chain: WI-15 -> WI-16
- Covers: AC-23, EC-12
- Skills to use: none
- Description: When indexing a finished video fails, retry up to 3 times (with a short back-off; use a plain loop or `spring-retry` if added; keep Java 7), logging each retry, then log the final failure including the video id. The `Video` stays in Mongo (and in Mongo-backed lists) and is simply absent from /search. Applies to the async upload path; the same helper is used by `updateVideo`'s index write if trivial. A failed index must not remove the stored video.
- Files likely touched: `service/impl/VideoServiceImpl.java` or the async worker from WI-15, a small `IndexingRetry` helper, tests
- Acceptance criteria:
  - With the search repository stubbed to fail twice then succeed, the video is indexed and two retry lines are logged.
  - With it failing every time, 3 retries are logged, a final failure line is logged, the video is in GET /videos and absent from /search.

### WI-17: Fast and avoidable startup reindex
- Module: vcp
- Stack: backend
- Depends on: WI-2
- Parallel-safe with: none in single-agent mode (in multi-agent mode only touches `ElasticSearchIndexingService` and its config, so it could run beside WI-14..WI-16)
- Chain: none
- Covers: AC-25, AC-26, EC-13
- Skills to use: engineering-paved-path:onion-architecture
- Description: Rework `ElasticSearchIndexingService.postConstruct`. (1) Keep the `index.all.during.startup` property name. When true, first compare the document count in the video index with `videoRepository.count()`; if the index exists and the counts are equal, log that the reindex is skipped and do nothing (no "Clear old index" line; search is available at once) (AC-26). If they differ, or the index is missing, rebuild. Add a new property (e.g. `index.force.reindex`, default false) to force a rebuild, which is also needed after WI-20 changes the document shape; and do not rely only on counts (Open question #7): optionally also compare the maximum `created_date`. (2) The rebuild reads videos in pages (e.g. 500) with the WI-5 reference resolver applied so owners/categories are not lazily loaded one by one, and writes with a bulk index request (`ElasticsearchTemplate.bulkIndex` / `IndexQuery` lists), not one `save` per video; log a single progress line per batch instead of one line per video. Index 10,000 videos in <= 60 s.
- Files likely touched: `service/impl/ElasticSearchIndexingService.java`, `repository/search/VideoSearchRepository.java` (only if needed), `src/main/resources/application.properties` (new property default), test resources, tests
- Acceptance criteria:
  - 10,000 videos are reindexed in <= 60 s between the "Start indexing" and "Finish indexing" log lines (AC-25).
  - Restart with equal counts: no "Clear old index" line and /search works from the first request (AC-26).
  - With `index.force.reindex=true` a rebuild happens regardless of counts.

### CP-2: Tier 2 checkpoint = AC-36 run (not a work item)
Build with every Tier 3 item absent. Run the WI-1 suite plus the Tier 2 checks; AC-1..AC-26 must pass. Record in `docs/perf/results.md`. This is the gate for starting Tier 3.

### Tier 3 - platform and infrastructure (coarse grain; prerequisites first)

Rules for all Tier 3 items (AC-36, AC-8): each one is switchable off by configuration or is a separate build step; the CP-2 suite must still pass with all of them off; the contract snapshot must pass with them on.

### WI-19: Tier 3 decision record (prerequisite for WI-21 .. WI-25)
- Module: vcp
- Stack: cross
- Depends on: CP-2
- Parallel-safe with: none
- Chain: none
- Covers: AC-27..AC-33 (preparation)
- Skills to use: none
- Description: Decisions needed from the user before building (write them down in `docs/plans/performance-tier3-decisions.md` as a short record; not code): target Java version and Spring/Spring Security/Spring Data/Jackson versions; what replaces the embedded Elasticsearch node (embedded `Node` is not supported by current Spring Data Elasticsearch, so AC-27 probably forces an external Elasticsearch or another search approach); replacement front-end framework; shared-cache product (Redis was named) and its failure behaviour; CDN vendor and how fallback to origin works (Open question #9); transcoding tool and renditions (Open question #8); how a `search document` is shaped (WI-20). The Tier 3 items below are not started until the relevant decision exists.
- Files likely touched: `docs/plans/performance-tier3-decisions.md`
- Acceptance criteria:
  - Each product choice above has a named decision, owner and date, or an explicit "deferred" marker.

### WI-20: Search document data minimisation and hydration
- Module: vcp
- Stack: backend
- Depends on: CP-2 (builds on WI-17 and WI-5); does not need any other Tier 3 decision
- Parallel-safe with: none
- Chain: none
- Covers: AC-34, AC-35, EC-15, EC-16, AC-8 (guard)
- Skills to use: engineering-paved-path:onion-architecture, engineering-paved-path:security
- Description: Index a dedicated `VideoSearchDocument` (id, title, owner name/surname, company name, category name, plus anything search ranks on) instead of the whole `Video` with its full `User`. It must contain no password, recovery code or raw email (AC-34). For /search responses, take the hit ids from Elasticsearch, then load the videos from Mongo by id in one query and resolve owners and categories with the WI-5 resolver, keeping ES order. This keeps the response shape identical to GET /videos (AC-8; otherwise /search would silently lose `owner.email`, `password` etc.), makes `owner.avatar` come out through the same `GravatarAspect` path (AC-35), and stays within 3 Mongo queries (hits `$in` + owners `$in` + cached reference data). Change the write side in `VideoServiceImpl`, the async worker and `ElasticSearchIndexingService` to build the search document. Rebuild the existing index once (force reindex from WI-17) because old documents still hold the sensitive fields (EC-15). Search result ordering and the fuzzy query stay unchanged.
- Files likely touched: `model/` (new search document), `repository/search/VideoSearchRepository.java`, `service/impl/VideoServiceImpl.java`, `service/impl/ElasticSearchIndexingService.java`, async worker, tests
- Acceptance criteria:
  - An Elasticsearch document for a video contains no `password`, `recoveryCode` or `email` (AC-34).
  - `/search` avatar URLs equal those in GET /videos for the same owner (AC-35).
  - Contract snapshot for /search unchanged; <= 3 Mongo queries per search.

### WI-21: Shared cache
- Module: vcp
- Stack: backend
- Depends on: CP-2, WI-19 (cache product)
- Parallel-safe with: WI-22, WI-23 once WI-19 is done (disjoint files)
- Chain: none
- Covers: AC-29, AC-30, EC-17
- Skills to use: engineering-paved-path:onion-architecture
- Description: Provide a second `CacheManager`/`ListCacheEviction` implementation backed by the chosen shared cache, enabled by configuration only (`WHERE a shared cache is enabled`), same 30 s TTL and eviction rules as WI-6. Cache errors (connection down, timeout) are caught, logged and treated as a miss so the request is answered from MongoDB with 200 (AC-30); use short timeouts. Values are stored in a serialisable form that does not contain the lazily-loaded proxies. Default stays the in-process cache from WI-6.
- Files likely touched: `config/CacheConfig.java`, new shared-cache adapter classes, `pom.xml`, `application.properties`, tests (integration with the product, two-instance test)
- Acceptance criteria:
  - With two instances, a result cached by A is served by B with 0 Mongo queries; eviction on one is seen by the other (AC-29).
  - With the cache stopped, GET /videos/latest returns 200 and an error is logged (AC-30).

### WI-22: CDN
- Module: vcp
- Stack: cross
- Depends on: CP-2 (relies on WI-7, WI-8, WI-9), WI-19 (vendor, fallback mechanism)
- Parallel-safe with: WI-21, WI-23 after WI-19
- Chain: none
- Covers: AC-31, AC-32, EC-17
- Skills to use: none
- Description: Add a configurable CDN base URL (empty by default). When set, `index.html` asset URLs and the `thumbnails`/`videoUrl`/`avatar` URLs built by the server use the CDN host. The URL form of fields must not change when the CDN is off (AC-8). Provide origin fallback at the configuration level as chosen in WI-19 (for example CDN failover to origin, or a front-end `onerror` fallback for media). Verify the CDN passes through `Range`, `Accept-Encoding` and conditional headers from WI-7..WI-9.
- Files likely touched: `application.properties`, URL-building code (`VideoProcessorServiceImpl`, `FileStorageImageService`, `GravatarAspect`), `index.html` substitution in the build, docs
- Acceptance criteria:
  - With the CDN on, URLs point to the CDN host; 206 and 304 are returned through it (AC-31).
  - With the CDN blocked, the home page loads and a video plays from the origin (AC-32).
  - With the CDN off, the contract snapshot is unchanged.

### WI-23: Background transcoding
- Module: vcp
- Stack: backend
- Depends on: CP-2 (WI-15 pipeline, WI-14), WI-19 (tool and renditions)
- Parallel-safe with: WI-21, WI-22 after WI-19 (but touches the async worker, so not with any other change to it)
- Chain: none
- Covers: AC-33
- Skills to use: engineering-paved-path:onion-architecture, engineering-paved-path:security
- Description: Add a transcoding step to the WI-15 async worker, behind a feature flag: produce a web-optimised rendition (metadata at the start of the file, height <= 720 px) with the tool chosen in WI-19, store it and use it as `videoUrl`. Never build the command line from client input (use fixed arguments and server-generated paths); apply a timeout and a bounded concurrency; failure follows the AC-22 cleanup rules or falls back to the original upload (decision in WI-19). The `videoUrl` field format is unchanged.
- Files likely touched: new `service/TranscodingService.java` + impl, async worker, `application.properties`, tests
- Acceptance criteria:
  - The stored rendition has height <= 720 and its metadata at the start of the file (AC-33).
  - Measured time to first frame <= 3 s on a 5 Mbit/s throttled connection for a 100 MB source (AC-33).
  - With the flag off, WI-15 behaviour and AC-20 are unchanged.

### WI-24: Platform upgrade
- Module: vcp
- Stack: cross
- Depends on: CP-2, WI-19 (versions, search engine decision)
- Parallel-safe with: none - changes `pom.xml` and nearly every package; do not run beside anything else
- Chain: none
- Covers: AC-27, AC-8, AC-36
- Skills to use: engineering-paved-path:onion-architecture, engineering-paved-path:security
- Description: Upgrade Java target, Spring, Spring Security, Spring Data (Mongo, Elasticsearch), Jackson, logback, JCodec, jjwt and the remaining libraries in `pom.xml` to supported releases per WI-19, in stages (build and test after each stage), via `pom.xml` and the Maven dependency workflow, never by editing `WEB-INF/lib`. Expect: namespace/API changes for Servlet and Spring Security configuration, the Elasticsearch embedded-node replacement, the Mongo driver upgrade (check that WI-2 indexes and WI-4 atomic updates still behave), test infrastructure changes (embedded Mongo, Mockito, JUnit). After the upgrade, Java 8+ idioms may replace the Java 7 workarounds, only if the user wants (not required). Run the dependency scan.
- Files likely touched: `pom.xml`, `config/**`, `security/**`, `repository/**`, tests, many others
- Acceptance criteria:
  - Dependency scan shows zero critical findings; the build and all existing tests pass (AC-27).
  - Contract snapshot passes (AC-8); the full CP-2 suite passes on the upgraded stack.

### WI-25: Replace AngularJS
- Module: vcp
- Stack: ui
- Depends on: CP-2 (WI-11..WI-13 define the budgets), WI-19 (framework choice)
- Parallel-safe with: WI-21..WI-23 after WI-19 (front end only); independent of WI-24
- Chain: none
- Covers: AC-28, AC-14..AC-19 (re-check on the new front end)
- Skills to use: depends on the framework chosen in WI-19; if React/Next.js is chosen use engineering-paved-path:frontend-architecture and engineering-paved-path:react-best-practices (and react-testing-library for the tests)
- Description: Re-implement the existing screens and routes (public list/details/search, auth, forgot/recovery, profile, upload, admin) in the chosen framework, served from the same WAR or a separate build artifact as decided in WI-19, consuming the unchanged JSON API. Migrate in slices (public pages first, admin last) so the old UI keeps working until each slice is replaced. Reapply: bundles under the AC-14 budget, admin code loaded only for admins, no video preload, Load more appends without re-creating cards, minified output.
- Files likely touched: `src/main/webapp/**` or a new front-end folder per WI-19, `pom.xml` build integration
- Acceptance criteria:
  - End-to-end test of the existing user flows passes on the new front end (AC-28).
  - The AC-14..AC-19 checks from the WI-1 harness pass on the new front end.

### Optional and pending items (NOT committed, not counted)

- OPT-A (pending Open question #11): stop returning `owner.password`, `recoveryCode`, `email` in API responses. Only plan after the user decides, and note it deliberately changes the response shape, so AC-8 and the WI-1 snapshot must be updated by decision, not drift. Sketch of what it touches: a Jackson mix-in or `@JsonIgnore`/`@JsonView` on `model/User.java` for those three fields (registered in `WebMvcConfig.configureMessageConverters`); server-side uses that need the email (`GravatarAspect.rewriteAvatar`, notifications, login) read the entity, not the JSON; check the endpoints where a user legitimately needs their own email (profile edit, admin users list/details, `UserController`) and keep it there via a separate view or DTO; front end: grep showed no use of `owner.email|password|recoveryCode` in `static/appjs` or `partials`, but confirm the profile and admin screens; update the contract snapshot and tests; it also shrinks list payloads. WI-20 reduces the risk for /search; OPT-A would apply to all endpoints. Blocks nothing in Tiers 1-2.
- OPT-B (spec suggestion #12): fix the `videos[v.id.viewed]` bug in `static/appjs/app-directives.js:59-66` so every video on a page is counted once. Needs go-ahead; belongs next to WI-4 and is a front-end-only change.
- OPT-C (Open question #5, suggestion yes): additive "processing" state visible to the uploader. With the UploadJob design (WI-15) this is a small new endpoint (e.g. GET of the user's pending jobs) plus a UI badge; additive API only, so allowed by AC-8 only if the user approves.

### Multi-agent notes (only if the user later switches mode)
- Tier 1 parallel sets. Suggested chains: Chain A (backend data path): WI-2 -> WI-4 -> WI-5 -> WI-6. Chain B (HTTP delivery): WI-3 -> WI-7 -> WI-8 -> WI-9 (all touch `WebMvcConfig`/`ApplicationInitializer`). Chain C (images): WI-10 -> WI-14. Chain D (front end): WI-11 -> WI-12 -> WI-13. A, B, C and D are file-disjoint except `WebMvcConfig` (B only) and `JCodecThumbnailService` (C only), so they can run in parallel worktrees after WI-1. WI-14 is tiny and mechanical.
- Tier 2: WI-17 (reindex) is file-disjoint from WI-15/WI-16 and can run in parallel with them after Chain A; WI-15 -> WI-16 is one chain.
- Tier 3: after CP-2 and WI-19, WI-20, WI-21, WI-22, WI-25 are mostly disjoint; WI-23 shares the async worker with WI-20; WI-24 must run alone.
- Worktree handoff: `WEB-INF/lib` is generated by `mvn package` into the source tree; each worktree should build separately and not commit it.

## Test plan
Style: the project uses JUnit 4 + Mockito unit tests for `*ServiceImpl` (one test class per service impl) and embedded-Mongo integration tests (`*ServiceIntegrationTest`). There is no testing doc; follow that style, add integration tests where behaviour depends on Mongo (indexes, atomic updates, query counts, cache), unit tests for pure logic (page clamping, image scaling, range parsing, retry loop).
- WI-1: the harness itself is the test; the contract test covers AC-8 for all touched endpoints.
- WI-2: integration `explain` tests (AC-3, AC-4), duplicate key test (AC-6).
- WI-3: MVC test with `MockMvc` for `size`/`page`/`query` edge values (AC-7, EC-8).
- WI-4: integration concurrency tests with a thread pool (AC-5 N=50, AC-6); update `VideoStatisticServiceImplTest`.
- WI-5: integration query-count tests for 5 endpoints at size 1 and 50 (AC-1), empty/beyond-last page (EC-1), dangling reference (EC-2), idempotent avatar rewrite; load test p95 (AC-2).
- WI-6: integration test counting Mongo queries across two requests (AC-12), eviction after delete/update (AC-13), TTL expiry with a controllable clock/short TTL.
- WI-7/8/9: `MockMvc` or embedded-servlet tests on headers/status (AC-9, AC-10, AC-11, EC-7), path traversal test.
- WI-10: unit tests on image dimensions and the megapixel cap (AC-19).
- WI-11/12/13: browser automation with a network log (count, bytes, requested URLs): AC-14, AC-15, AC-16, AC-17, AC-18 (element identity), plus a manual smoke list of all routes. The tool (e.g. a headless browser run from the harness) is chosen in WI-1/WI-11; no front-end test infrastructure exists today.
- WI-14: handle-count test after 100 runs (AC-24).
- WI-15: integration test with a fake slow thumbnail step: response time, absence from lists until done, cleanup on failure/restart, single log line (AC-20..AC-22).
- WI-16: unit test with a failing search repository stub (AC-23).
- WI-17: 10,000-video timing test (AC-25), skip-on-equal-counts test (AC-26).
- Tier 3: WI-20 (AC-34, AC-35, contract), WI-21 (two-instance, cache down), WI-22 (headers through CDN, CDN blocked), WI-23 (rendition height/metadata, first-frame time), WI-24 (scan + all tests), WI-25 (E2E flows + AC-14..AC-19).
- CP-1/CP-2 re-run the whole suite; CP-2 is the AC-36 evidence.

## Risks / open questions
- R-1 (spec problem, AC-1): a list needs the page query, the count, owners, categories and the owner's company; that is more than 3 queries with plain lazy DBRefs. The plan reaches 3 by batch-loading owners and caching categories/companies in process. If the user prefers denormalisation or accepts 4-5 queries, the AC needs to change. Also check that serialising `owner.authorities` does not add per-owner queries.
- R-2 (spec problem, AC-14): 18 poster images plus 18 avatars already exceed "at most 15 requests" if all load eagerly. The plan assumes lazy-loaded below-the-fold images and defines "first interaction" as: DOM ready, above-the-fold images loaded, the 3 API calls done. The user must confirm this definition (or revise the number).
- R-3 (spec problem, AC-12 vs AC-13): "view count changes ordering" must evict, but evicting on every view defeats the 30 s cache. The plan evicts Popular only if the viewed video is in, or newly qualifies for, the cached page; Latest is never evicted by views. Confirm.
- R-4 (spec problem, AC-34/35 vs AC-8): removing owner email/password from search documents would change /search response fields; WI-20 avoids that by hydrating from Mongo. Whether the API should hide those fields at all is Open question #11 (OPT-A, undecided).
- R-5: the spec Status is `draft`, not `approved`.
- R-6: `createdDate` is a String; sorting is correct only for equal-length epoch-millis strings. Safe until 2286; documented, not changed.
- R-7: existing duplicate `VideoStatistic` rows block the unique index; the dedupe step in WI-2 changes data and needs the operator's/user's awareness.
- R-8: AC-27 likely forces dropping the embedded Elasticsearch node (a deployment change: external ES or another search approach), which is a large decision (WI-19).
- R-9: AC-11 requires custom Range code because Spring 4.2.5 does not provide it; AC-9 needs an in-app filter because container compression cannot be assured from the WAR.
- R-10: Open question #7: a count match does not prove the index content matches; WI-17 adds a force property and optionally a last-created marker. Updates do not change counts.
- R-11: the check commands are undocumented; `mvn` was not on PATH on the planning machine and the local JDK is 11 vs source/target 1.7. The first implementer must confirm the working build/test command and the embedded-Mongo download needs network.
- R-12: scale/hardware (Open question #10) is undefined; the WI-1 baseline must state the machine used so the budgets are comparable.
- Defaults used in place of missing input: plans directory `docs/plans/`, single-agent mode (no user answer available), spec values marked [ASSUMPTION] implemented as stated.
