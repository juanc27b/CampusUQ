package co.edu.uniquindio.campusuq.util;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.vo.Information;
import co.edu.uniquindio.campusuq.vo.InformationCategory;
import co.edu.uniquindio.campusuq.vo.Item;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class ItemsPresenter {

    public ArrayList<Item> informationItems;
    public ArrayList<Item> servicesItems;
    public ArrayList<Item> stateItems;
    public ArrayList<Item> communicationItems;
    public ArrayList<Item> institutionItems;
    public ArrayList<Item> libraryItems;

    public int[] circleColors;
    public ArrayList<Integer> colors;

    public ItemsPresenter() {
        circleColors = new int[7];
        circleColors[0] = R.drawable.circle_blue;
        circleColors[1] = R.drawable.circle_orange;
        circleColors[2] = R.drawable.circle_red;
        circleColors[3] = R.drawable.circle_yellow;
        circleColors[4] = R.drawable.circle_purple;
        circleColors[5] = R.drawable.circle_dark_blue;
        circleColors[6] = R.drawable.circle_green;

        colors = new ArrayList<Integer>();
        fillColors();
    }

    public int getColor() {
        if (colors.size() == 0) {
            fillColors();
        }
        int random = (int) (Math.random()*colors.size());
        int pos = colors.remove(random);
        return circleColors[pos];
    }

    public void fillColors() {
        colors.add(0,0);
        colors.add(1,1);
        colors.add(2,2);
        colors.add(3,3);
        colors.add(4,4);
        colors.add(5,5);
        colors.add(6,6);
    }

    public ArrayList<Item> getInformationItems() {
        if (informationItems == null) {
            informationItems = new ArrayList<Item>();
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_events, R.string.events, R.string.events_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_news, R.string.news, R.string.news_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_institution, R.string.institution, R.string.institution_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_directory, R.string.directory, R.string.directory_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_academic_offer, R.string.academic_offer, R.string.academic_offer_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_academic_calendar, R.string.academic_calendar, R.string.academic_calendar_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_employment_exchange, R.string.employment_exchange, R.string.employment_exchange_description));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_institutional_welfare, R.string.institutional_welfare, R.string.institutional_welfare_description));
        }
        return informationItems;
    }

    public ArrayList<Item> getServicesItems() {
        if (servicesItems == null) {
            servicesItems = new ArrayList<Item>();
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_university_map, R.string.university_map, R.string.university_map_description));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_library_services, R.string.library_services, R.string.library_services_description));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_radio, R.string.radio, R.string.radio_description));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_pqrsd_system, R.string.pqrsd_system, R.string.pqrsd_system_description));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_lost_objects, R.string.lost_objects, R.string.lost_objects_description));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_security_system, R.string.security_system, R.string.security_system_description));
        }
        return servicesItems;
    }

    public ArrayList<Item> getStateItems() {
        if (stateItems == null) {
            stateItems = new ArrayList<Item>();
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_restaurant, R.string.restaurant, R.string.restaurant_description));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_billboard_information, R.string.billboard_information, R.string.billboard_information_description));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_computer_rooms, R.string.computer_rooms, R.string.computer_rooms_description));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_parking_lots, R.string.parking_lots, R.string.parking_lots_description));
        }
        return stateItems;
    }

    public ArrayList<Item> getCommunicationItems() {
        if (communicationItems == null) {
            communicationItems = new ArrayList<Item>();
            communicationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_institutional_mail, R.string.institutional_mail, R.string.institutional_mail_description));
            communicationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_web_page, R.string.web_page, R.string.web_page_description));
            communicationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_ecotic, R.string.ecotic, R.string.ecotic_description));
        }
        return communicationItems;
    }

    public ArrayList<Item> getLibraryItems() {
        if (libraryItems == null) {
            libraryItems = new ArrayList<Item>();
            libraryItems.add(
                    new Item(getColor(), R.drawable.ic_digital_repository, R.string.digital_repository, R.string.digital_repository_description));
            libraryItems.add(
                    new Item(getColor(), R.drawable.ic_public_catalog, R.string.public_catalog, R.string.public_catalog_description));
            libraryItems.add(
                    new Item(getColor(), R.drawable.ic_databases, R.string.databases, R.string.databases_description));
        }
        return libraryItems;
    }

    public ArrayList<Item> getInstitutionItems() {
        if (institutionItems == null) {
            institutionItems = new ArrayList<Item>();
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_mission_vision, R.string.mission_vision, R.string.mission_vision_description));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_quality_policy, R.string.quality_policy, R.string.quality_policy_description));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_axes_pillars_objectives, R.string.axes_pillars_objectives, R.string.axes_pillars_objectives_description));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_symbols, R.string.symbols, R.string.symbols_description));
        }
        return institutionItems;
    }


    public static String[] getInformation(String categoryName, Context context) {
        String[] info = new String[2];
        String link = "";
        String content = "";

        InformationsSQLiteController dbController = new InformationsSQLiteController(context, 1);

        if (MainActivity.haveNetworkConnection(context)) {
            boolean updateInformations = false;
            ArrayList<InformationCategory> updatedCategories = InformationsServiceController.getInformationCategories();
            for (InformationCategory category : updatedCategories) {
                ArrayList<InformationCategory> oldCategories = dbController.selectCategory(
                        InformationsSQLiteController.CAMPOS_CATEGORIA[0] + " = ?", new String[]{category.get_ID()});
                if (oldCategories.size() == 0) {
                    updateInformations = true;
                    dbController.insertCategory(category.get_ID(), category.getName(), category.getLink(), category.getDate());
                } else if (oldCategories.get(0).getDate().compareTo(category.getDate()) < 0) {
                    updateInformations = true;
                    // update category
                }
                if (updateInformations){
                    ArrayList<Information> updatedInformations = InformationsServiceController.getInformations(category.get_ID());
                    for (Information information : updatedInformations) {
                        ArrayList<Information> olds = dbController.select(
                                InformationsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{information.get_ID()});
                        if (olds.size() > 0) {
                            dbController.update(information.get_ID(), information.getCategory_ID(),
                                    information.name, information.getContent());
                        } else {
                            dbController.insert(information.get_ID(), information.getCategory_ID(),
                                    information.name, information.getContent());
                        }
                    }
                }
            }
        }

        ArrayList<InformationCategory> categories = dbController.selectCategory(
                InformationsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{categoryName});
        ArrayList<Information> informations;
        if (categories.size() > 0) {
            link = categories.get(0).getLink();
            informations = dbController.select(
                    InformationsSQLiteController.CAMPOS_TABLA[1] + " = ?", new String[]{categories.get(0).get_ID()});
        } else {
            informations = new ArrayList<>();
        }

        for (Information information : informations) {
            content += information.getContent();
        }

        info[0] = link;
        info[1] = content;
        return info;
    }


}
