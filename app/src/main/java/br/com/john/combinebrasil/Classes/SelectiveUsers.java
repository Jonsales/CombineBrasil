package br.com.john.combinebrasil.Classes;

/**
 * Created by GTAC on 19/08/2017.
 */

public class SelectiveUsers extends Selective {
    private String urlImage="";
    private String nameAdmin = "";
    private boolean isSelectiveAdmin;
    private String nameTeam;

    public SelectiveUsers(){}

    public SelectiveUsers(String id, String title, String team, String user, String date, String codeSelective,
                          boolean canSync, String city, String neighbothood, String state, String street,
                          String postalCode, String notes, String address, String nameAdmin,
                          boolean isSelectiveAdmin, String urlImage, String nameTeam) {
        setId(id);
        setTitle(title);
        setTeam(team);
        setUser(user);
        setDate(date);
        setCodeSelective(codeSelective);
        setCanSync(canSync);
        setCity(city);
        setNeighborhood(neighbothood);
        setState(state);
        setStreet(street);
        setPostalCode(postalCode);
        setNotes(notes);
        setAddress(address);
        this.nameAdmin = nameAdmin;
        this.urlImage = urlImage;
        this.isSelectiveAdmin = isSelectiveAdmin;
        this.nameTeam= nameTeam;
    }


    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public boolean isSelectiveAdmin() {
        return isSelectiveAdmin;
    }

    public void setSelectiveAdmin(boolean selectiveAdmin) {
        isSelectiveAdmin = selectiveAdmin;
    }

    public String getNameAdmin() {
        return nameAdmin;
    }

    public void setNameAdmin(String nameAdmin) {
        this.nameAdmin = nameAdmin;
    }

    public String getNameTeam() {
        return nameTeam;
    }

    public void setNameTeam(String nameTeam) {
        this.nameTeam = nameTeam;
    }
}
