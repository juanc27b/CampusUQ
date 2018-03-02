package co.edu.uniquindio.campusuq.vo;

/**
 * Created by Juan Camilo on 28/02/2018.
 */

public class Program {

    private String _ID;
    private String category_ID;
    private String faculty_ID;
    private String name;
    private String history;
    private String historyLink;
    private String historyDate;
    private String missionVision;
    private String missionVisionLink;
    private String missionVisionDate;
    private String curriculum;
    private String curriculumLink;
    private String curriculumDate;
    private String profiles;
    private String profilesLink;
    private String profilesDate;
    private String contact;

    public Program(String _ID, String category_ID, String faculty_ID, String name,
                   String history, String historyLink, String historyDate,
                   String missionVision, String missionVisionLink, String missionVisionDate,
                   String curriculum, String curriculumLink, String curriculumDate,
                   String profiles, String profilesLink, String profilesDate, String contact) {
        this._ID = _ID;
        this.category_ID = category_ID;
        this.faculty_ID = faculty_ID;
        this.name = name;
        this.history = history;
        this.historyLink = historyLink;
        this.historyDate = historyDate;
        this.missionVision = missionVision;
        this.missionVisionLink = missionVisionLink;
        this.missionVisionDate = missionVisionDate;
        this.curriculum = curriculum;
        this.curriculumLink = curriculumLink;
        this.curriculumDate = curriculumDate;
        this.profiles = profiles;
        this.profilesLink = profilesLink;
        this.profilesDate = profilesDate;
        this.contact = contact;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getCategory_ID() {
        return category_ID;
    }

    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getHistoryLink() {
        return historyLink;
    }

    public void setHistoryLink(String historyLink) {
        this.historyLink = historyLink;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public String getMissionVision() {
        return missionVision;
    }

    public void setMissionVision(String missionVision) {
        this.missionVision = missionVision;
    }

    public String getMissionVisionLink() {
        return missionVisionLink;
    }

    public void setMissionVisionLink(String missionVisionLink) {
        this.missionVisionLink = missionVisionLink;
    }

    public String getMissionVisionDate() {
        return missionVisionDate;
    }

    public void setMissionVisionDate(String missionVisionDate) {
        this.missionVisionDate = missionVisionDate;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    public String getCurriculumLink() {
        return curriculumLink;
    }

    public void setCurriculumLink(String curriculumLink) {
        this.curriculumLink = curriculumLink;
    }

    public String getCurriculumDate() {
        return curriculumDate;
    }

    public void setCurriculumDate(String curriculumDate) {
        this.curriculumDate = curriculumDate;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public String getProfilesLink() {
        return profilesLink;
    }

    public void setProfilesLink(String profilesLink) {
        this.profilesLink = profilesLink;
    }

    public String getProfilesDate() {
        return profilesDate;
    }

    public void setProfilesDate(String profilesDate) {
        this.profilesDate = profilesDate;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
