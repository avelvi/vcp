package com.aivlev.vcp.dto;

/**
 * Created by aivlev on 6/14/16.
 */
public class StatisticsDto {

    private long usersCount;
    private long videosCount;
    private long categoriesCount;
    private long companiesCount;

    public StatisticsDto() {
    }

    public StatisticsDto(long usersCount, long videosCount, long categoriesCount, long companiesCount) {
        this.usersCount = usersCount;
        this.videosCount = videosCount;
        this.categoriesCount = categoriesCount;
        this.companiesCount = companiesCount;
    }

    public long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(long usersCount) {
        this.usersCount = usersCount;
    }

    public long getVideosCount() {
        return videosCount;
    }

    public void setVideosCount(long videosCount) {
        this.videosCount = videosCount;
    }

    public long getCategoriesCount() {
        return categoriesCount;
    }

    public void setCategoriesCount(long categoriesCount) {
        this.categoriesCount = categoriesCount;
    }

    public long getCompaniesCount() {
        return companiesCount;
    }

    public void setCompaniesCount(long companiesCount) {
        this.companiesCount = companiesCount;
    }
}
