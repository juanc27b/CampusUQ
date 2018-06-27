package co.edu.uniquindio.campusuq.programs;

/**
 * Clase que almacena la informacion de un programa (objeto de valor para la funcionalidad de Oferta
 * academica), y permite transmitirlo desde el servidor y  desde y hacia la base de datos local.
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

    /**
     * Constructor de un programa que se encarga de asignar valores a cada una de sus variables.
     * @param _ID ID del programa.
     * @param category_ID ID de la categoria de programa.
     * @param faculty_ID ID de la facultad de programa.
     * @param name Nombre del programa.
     * @param history Historia del programa.
     * @param historyLink Enlace de la historia del programa.
     * @param historyDate Fecha de la historia del programa.
     * @param missionVision Mision y vision del programa.
     * @param missionVisionLink Enlace de la mision y vision del programa.
     * @param missionVisionDate Fecha de la mision y vision del programa.
     * @param curriculum Plan de estudios del programa.
     * @param curriculumLink Elnace del plan de estudios del programa.
     * @param curriculumDate Fecha del plan de estudios del programa.
     * @param profiles Perfiles del programa.
     * @param profilesLink Enlace de los perfiles del programa.
     * @param profilesDate Fecha de los perfiles del programa.
     * @param contact Informacion de contacto del programa.
     */
    Program(String _ID, String category_ID, String faculty_ID, String name, String history,
            String historyLink, String historyDate, String missionVision, String missionVisionLink,
            String missionVisionDate, String curriculum, String curriculumLink,
            String curriculumDate, String profiles, String profilesLink, String profilesDate,
            String contact) {
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

    /**
     * Método para obtener la ID del programa.
     * @return ID del programa.
     */
    public String get_ID() {
        return _ID;
    }

    /**
     * Método para establecer la ID del programa.
     * @param _ID Nueva ID para el programa.
     */
    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    /**
     * Método para obtener la ID de la categoria de programa.
     * @return ID de la categoria de programa.
     */
    public String getCategory_ID() {
        return category_ID;
    }

    /**
     * Método para establecer la ID de la categoria de programa.
     * @param category_ID Nueva ID para la categoria de programa.
     */
    public void setCategory_ID(String category_ID) {
        this.category_ID = category_ID;
    }

    /**
     * Método para obtener la ID de la facultad de programa.
     * @return ID de la facultad de programa.
     */
    public String getFaculty_ID() {
        return faculty_ID;
    }

    /**
     * Método para establecer la ID de la facultad de programa.
     * @param faculty_ID Nueva ID para la facultad de programa.
     */
    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    /**
     * Método para obtener el nombre del programa.
     * @return Nombre del programa.
     */
    public String getName() {
        return name;
    }

    /**
     * Método para establecer el nombre del programa.
     * @param name Nuevo nombre para el programa.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Método para obtener la historia del programa.
     * @return Historia del programa.
     */
    public String getHistory() {
        return history;
    }

    /**
     * Método para establecer la historia del programa.
     * @param history Nueva historia para el programa.
     */
    public void setHistory(String history) {
        this.history = history;
    }

    /**
     * Método para obtener el enlace de la historia del programa.
     * @return Enlace de la historia del programa.
     */
    public String getHistoryLink() {
        return historyLink;
    }

    /**
     * Método para establecer el enlace de la historia del programa.
     * @param historyLink Nuevo enlace de la historia para el programa.
     */
    public void setHistoryLink(String historyLink) {
        this.historyLink = historyLink;
    }

    /**
     * Método para obtener la fecha de la historia del programa.
     * @return Fecha de la historia del programa.
     */
    public String getHistoryDate() {
        return historyDate;
    }

    /**
     * Método para establecer la fecha de la historia del programa.
     * @param historyDate Nueva fecha de la historia para el programa.
     */
    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    /**
     * Método para obtener la mision y vision del programa.
     * @return Mision y vision del programa.
     */
    public String getMissionVision() {
        return missionVision;
    }

    /**
     * Método para establecer la mision y vision del programa.
     * @param missionVision Nueva mision y vision para el programa.
     */
    public void setMissionVision(String missionVision) {
        this.missionVision = missionVision;
    }

    /**
     * Método para obtener el enlace de la mision y vision del programa.
     * @return Enlace de la mision y vision del programa.
     */
    public String getMissionVisionLink() {
        return missionVisionLink;
    }

    /**
     * Método para establecer el enlace de la mision y vision del programa.
     * @param missionVisionLink Nuevo enlace de la mision y vision para el programa.
     */
    public void setMissionVisionLink(String missionVisionLink) {
        this.missionVisionLink = missionVisionLink;
    }

    /**
     * Método para obtener la fecha de la mision y vision del programa.
     * @return Fecha de la mision y vision del programa.
     */
    public String getMissionVisionDate() {
        return missionVisionDate;
    }

    /**
     * Método para establecer la fecha de la mision y vision del programa.
     * @param missionVisionDate Nueva fecha de la mision y vision para el programa.
     */
    public void setMissionVisionDate(String missionVisionDate) {
        this.missionVisionDate = missionVisionDate;
    }

    /**
     * Método para obtener el plan de estudios del programa.
     * @return Plan de estudios del programa.
     */
    public String getCurriculum() {
        return curriculum;
    }

    /**
     * Método para establecer el plan de estudios del programa.
     * @param curriculum Nuevo plan de estudios para el programa.
     */
    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    /**
     * Método para obtener el enlace del plan de estudios del programa.
     * @return Enlace del plan de estudios del programa.
     */
    public String getCurriculumLink() {
        return curriculumLink;
    }

    /**
     * Método para establecer el enlace del plan de estudios del programa.
     * @param curriculumLink Nuevo enlace del plan de estudios para el programa.
     */
    public void setCurriculumLink(String curriculumLink) {
        this.curriculumLink = curriculumLink;
    }

    /**
     * Método para obtener la fecha del plan de estudios del programa.
     * @return Fecha del plan de estudios del programa.
     */
    public String getCurriculumDate() {
        return curriculumDate;
    }

    /**
     * Método para establecer la fecha del plan de estudios del programa.
     * @param curriculumDate Nueva fecha del plan de estudios para el programa.
     */
    public void setCurriculumDate(String curriculumDate) {
        this.curriculumDate = curriculumDate;
    }

    /**
     * Método para obtener los perfiles del programa.
     * @return Perfiles del programa.
     */
    public String getProfiles() {
        return profiles;
    }

    /**
     * Método para establecer los perfiles del programa.
     * @param profiles Nuevos perfiles para el programa.
     */
    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    /**
     * Método para obtener el enlace de los perfiles del programa.
     * @return Enlace de los perfiles del programa.
     */
    public String getProfilesLink() {
        return profilesLink;
    }

    /**
     * Método para establecer el enlace de los perfiles del programa.
     * @param profilesLink Nuevo enlace de los perfiles para el programa.
     */
    public void setProfilesLink(String profilesLink) {
        this.profilesLink = profilesLink;
    }

    /**
     * Método para obtener la fecha de los perfiles del programa.
     * @return Fecha de los perfiles del programa.
     */
    public String getProfilesDate() {
        return profilesDate;
    }

    /**
     * Método para establecer la fecha de los perfiles del programa.
     * @param profilesDate Nueva fecha de los perfiles para el programa.
     */
    public void setProfilesDate(String profilesDate) {
        this.profilesDate = profilesDate;
    }

    /**
     * Método para obtener la informacion de contacto del programa.
     * @return Informacion de contacto del programa.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Método para establecer la informacion de contacto del programa.
     * @param contact Nueva informacion de contacto para el programa.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

}
