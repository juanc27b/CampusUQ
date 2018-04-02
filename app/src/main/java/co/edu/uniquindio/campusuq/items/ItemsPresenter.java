package co.edu.uniquindio.campusuq.items;

import android.content.Context;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.contacts.ContactsSQLiteController;
import co.edu.uniquindio.campusuq.events.EventsSQLiteController;
import co.edu.uniquindio.campusuq.informations.InformationsSQLiteController;
import co.edu.uniquindio.campusuq.contacts.Contact;
import co.edu.uniquindio.campusuq.contacts.ContactCategory;
import co.edu.uniquindio.campusuq.events.EventCategory;
import co.edu.uniquindio.campusuq.events.EventRelation;
import co.edu.uniquindio.campusuq.informations.Information;
import co.edu.uniquindio.campusuq.informations.InformationCategory;
import co.edu.uniquindio.campusuq.programs.ProgramsSQLiteController;
import co.edu.uniquindio.campusuq.programs.Program;
import co.edu.uniquindio.campusuq.programs.ProgramCategory;
import co.edu.uniquindio.campusuq.programs.ProgramFaculty;

/**
 * Created by Juan Camilo on 20/02/2018.
 */

public class ItemsPresenter {

    private ArrayList<Item> informationItems;
    private ArrayList<Item> servicesItems;
    private ArrayList<Item> stateItems;
    private ArrayList<Item> communicationItems;
    private ArrayList<Item> institutionItems;
    private ArrayList<Item> libraryItems;
    private ArrayList<Item> programItems;

    private static int[] circleColors = null;
    private static ArrayList<Integer> colors = new ArrayList<>();

    ItemsPresenter() {}

    public static int getColor() {
        if (colors.size() == 0) {
            fillColors();
        }
        int random = (int) (Math.random()*colors.size());
        int pos = colors.remove(random);
        return circleColors[pos];
    }

    private static void fillColors() {
        if (circleColors == null) {
            circleColors = new int[7];
            circleColors[0] = R.drawable.circle_blue;
            circleColors[1] = R.drawable.circle_orange;
            circleColors[2] = R.drawable.circle_red;
            circleColors[3] = R.drawable.circle_yellow;
            circleColors[4] = R.drawable.circle_purple;
            circleColors[5] = R.drawable.circle_dark_blue;
            circleColors[6] = R.drawable.circle_green;
        }
        colors.add(0,0);
        colors.add(1,1);
        colors.add(2,2);
        colors.add(3,3);
        colors.add(4,4);
        colors.add(5,5);
        colors.add(6,6);
    }

    ArrayList<Item> getInformationItems(Context context) {
        if (informationItems == null) {
            informationItems = new ArrayList<>();
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_events, context.getString(R.string.events), context.getString(R.string.events_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_news, context.getString(R.string.news), context.getString(R.string.news_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_institution, context.getString(R.string.institution), context.getString(R.string.institution_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_directory, context.getString(R.string.directory), context.getString(R.string.directory_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_academic_offer, context.getString(R.string.academic_offer), context.getString(R.string.academic_offer_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_academic_calendar, context.getString(R.string.academic_calendar), context.getString(R.string.academic_calendar_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_employment_exchange, context.getString(R.string.employment_exchange), context.getString(R.string.employment_exchange_description)));
            informationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_institutional_welfare, context.getString(R.string.institutional_welfare), context.getString(R.string.institutional_welfare_description)));
        }
        return informationItems;
    }

    ArrayList<Item> getServicesItems(Context context) {
        if (servicesItems == null) {
            servicesItems = new ArrayList<>();
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_university_map, context.getString(R.string.university_map), context.getString(R.string.university_map_description)));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_library_services, context.getString(R.string.library_services), context.getString(R.string.library_services_description)));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_radio, context.getString(R.string.radio), context.getString(R.string.radio_description)));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_pqrsd_system, context.getString(R.string.pqrsd_system), context.getString(R.string.pqrsd_system_description)));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_lost_objects, context.getString(R.string.lost_objects), context.getString(R.string.lost_objects_description)));
            servicesItems.add(
                    new Item(getColor(), R.drawable.ic_menu_security_system, context.getString(R.string.security_system), context.getString(R.string.security_system_description)));
        }
        return servicesItems;
    }

    ArrayList<Item> getStateItems(Context context) {
        if (stateItems == null) {
            stateItems = new ArrayList<>();
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_restaurant, context.getString(R.string.restaurant), context.getString(R.string.restaurant_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_billboard_information, context.getString(R.string.billboard_information), context.getString(R.string.billboard_information_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_computer_rooms, context.getString(R.string.computer_rooms), context.getString(R.string.computer_rooms_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_parking_lots, context.getString(R.string.parking_lots), context.getString(R.string.parking_lots_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_laboratories, context.getString(R.string.laboratories), context.getString(R.string.laboratories_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_studio_zones, context.getString(R.string.study_areas), context.getString(R.string.study_areas_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_cultural_and_sport, context.getString(R.string.cultural_and_sport), context.getString(R.string.cultural_and_sport_description)));
            stateItems.add(
                    new Item(getColor(), R.drawable.ic_menu_auditoriums, context.getString(R.string.auditoriums), context.getString(R.string.auditoriums_description)));
        }
        return stateItems;
    }

    ArrayList<Item> getCommunicationItems(Context context) {
        if (communicationItems == null) {
            communicationItems = new ArrayList<>();
            communicationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_institutional_mail, context.getString(R.string.institutional_mail), context.getString(R.string.institutional_mail_description)));
            communicationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_web_page, context.getString(R.string.web_page), context.getString(R.string.web_page_description)));
            communicationItems.add(
                    new Item(getColor(), R.drawable.ic_menu_ecotic, context.getString(R.string.ecotic), context.getString(R.string.ecotic_description)));
        }
        return communicationItems;
    }

    ArrayList<Item> getInstitutionItems(Context context) {
        if (institutionItems == null) {
            institutionItems = new ArrayList<>();
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_mission_vision, context.getString(R.string.mission_vision), context.getString(R.string.mission_vision_description)));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_quality_policy, context.getString(R.string.quality_policy), context.getString(R.string.quality_policy_description)));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_axes_pillars_objectives, context.getString(R.string.axes_pillars_objectives), context.getString(R.string.axes_pillars_objectives_description)));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_organization_chart, context.getString(R.string.organization_chart), context.getString(R.string.organization_chart_description)));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_normativity, context.getString(R.string.normativity), context.getString(R.string.normativity_description)));
            institutionItems.add(
                    new Item(getColor(), R.drawable.ic_symbols, context.getString(R.string.symbols), context.getString(R.string.symbols_description)));
        }
        return institutionItems;
    }

    ArrayList<Item> getLibraryItems(Context context) {
        if (libraryItems == null) {
            libraryItems = new ArrayList<>();
            libraryItems.add(
                    new Item(getColor(), R.drawable.ic_digital_repository, context.getString(R.string.digital_repository), context.getString(R.string.digital_repository_description)));
            libraryItems.add(
                    new Item(getColor(), R.drawable.ic_public_catalog, context.getString(R.string.public_catalog), context.getString(R.string.public_catalog_description)));
            libraryItems.add(
                    new Item(getColor(), R.drawable.ic_databases, context.getString(R.string.databases), context.getString(R.string.databases_description)));
        }
        return libraryItems;
    }

    ArrayList<Item> getProgramItems(Context context) {
        if (programItems == null) {
            programItems = new ArrayList<>();
            programItems.add(
                    new Item(getColor(), 0, context.getString(R.string.history), context.getString(R.string.history_description)));
            programItems.add(
                    new Item(getColor(), 0, context.getString(R.string.program_mission_vision), context.getString(R.string.program_mission_vision_description)));
            programItems.add(
                    new Item(getColor(), 0, context.getString(R.string.curriculum), context.getString(R.string.curriculum_description)));
            programItems.add(
                    new Item(getColor(), 0, context.getString(R.string.profiles), context.getString(R.string.profiles_description)));
            programItems.add(
                    new Item(getColor(), 0, context.getString(R.string.program_contact), context.getString(R.string.program_contact_description)));
        }
        return programItems;
    }

    public static String[] getInformation(String categoryName, Context context) {
        String[] info = new String[2];
        String link = null;
        String content = "";

        InformationsSQLiteController dbController = new InformationsSQLiteController(context, 1);

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

        dbController.destroy();

        info[0] = link;
        info[1] = content;
        return info;
    }

    public static ArrayList<Item> getContactCategories(Context context) {

        ArrayList<Item> items = new ArrayList<>();

        ContactsSQLiteController dbController = new ContactsSQLiteController(context, 1);

        ArrayList<ContactCategory> categories = dbController.selectCategory(null, null);

        for (ContactCategory category : categories) {
            ArrayList<Contact> contacts = dbController.select(
                    ContactsSQLiteController.columns[1] + " = ?", new String[]{category.get_ID()});
            Item item = new Item(getColor(), 0, category.getName(),
                    contacts.size()+" "+context.getString(R.string.contacts));
            items.add(item);
        }

        dbController.destroy();

        return items;
    }

    static ArrayList<Item> getContacts(String categoryName, Context context) {

        ArrayList<Item> items = new ArrayList<>();

        ContactsSQLiteController dbController = new ContactsSQLiteController(context, 1);

        ArrayList<ContactCategory> categories = dbController.selectCategory(
                ContactsSQLiteController.categoryColumns[1] + " = ?", new String[]{categoryName});
        if (categories.size() > 0) {
            ContactCategory category = categories.get(0);

            ArrayList<Contact> contacts = dbController.select(
                    ContactsSQLiteController.columns[1] + " = ?", new String[]{category.get_ID()});

            for (Contact contact : contacts) {
                String description = context.getString(R.string.address)+": "+contact.getAddress()+"\n"+
                        context.getString(R.string.phone)+": "+contact.getPhone()+"\n"+
                        context.getString(R.string.email)+": "+contact.getEmail()+"\n"+
                        context.getString(R.string.charge)+": "+contact.getCharge()+"\n"+
                        context.getString(R.string.additional_information)+": "+contact.getAdditionalInformation();
                Item item = new Item(R.drawable.circle_white, R.drawable.ic_person, contact.getName(), description);
                items.add(item);
            }
        }

        dbController.destroy();

        return items;
    }

    public static ArrayList<Item> getPrograms(Context context) {

        ArrayList<Item> items = new ArrayList<>();

        ProgramsSQLiteController dbController = new ProgramsSQLiteController(context, 1);

        ArrayList<ProgramCategory> categories = dbController.selectCategory(null, null);
        if (categories.size() > 0) {
            ProgramCategory undergraduate = categories.get(0).getName().equals("Pregrado") ? categories.get(0) : categories.get(1);
            ProgramCategory postgraduate = categories.get(0).getName().equals("Posgrado") ? categories.get(0) : categories.get(1);

            ArrayList<ProgramFaculty> faculties = dbController.selectFaculty(null, null);

            for (ProgramFaculty faculty : faculties) {

                int color = 0;
                switch (faculty.getName()) {
                    case "Facultad de Ingeniería":
                        color = R.drawable.circle_blue;
                        break;
                    case "Facultad de Educación":
                        color = R.drawable.circle_orange;
                        break;
                    case "Facultad de Ciencias Humanas y Bellas Artes":
                        color = R.drawable.circle_red;
                        break;
                    case "Facultad de Ciencias Básicas y Tecnologías":
                        color = R.drawable.circle_yellow;
                        break;
                    case "Facultad de Ciencias Económicas y Administrativas":
                        color = R.drawable.circle_purple;
                        break;
                    case "Facultad Ciencias De La Salud":
                        color = R.drawable.circle_dark_blue;
                        break;
                    case "Facultad de Ciencias Agroindustriales":
                        color = R.drawable.circle_green;
                        break;
                    default:
                        break;
                }

                ArrayList<Program> programs = dbController.select(
                        ProgramsSQLiteController.CAMPOS_TABLA[2] + " = ?", new String[]{faculty.get_ID()});

                for (Program program : programs) {
                    String categoria = program.getCategory_ID().equals(undergraduate.get_ID()) ?
                            undergraduate.getName() : postgraduate.getName();
                    Item item = new Item(color, 0, program.getName(),
                            categoria+", "+faculty.getName());
                    items.add(item);
                }

            }
        }

        dbController.destroy();

        return items;
    }

    static String[] getProgramContent(String name, String type, Context context) {
        String[] info = new String[2];
        String link = null;
        String content = "";

        ProgramsSQLiteController dbController = new ProgramsSQLiteController(context, 1);

        ArrayList<Program> programs = dbController.select(
                ProgramsSQLiteController.CAMPOS_TABLA[3] + " = ?", new String[]{name});
        if (programs.size() > 0) {
            if (context.getString(R.string.history).equals(type)) {
                link = programs.get(0).getHistoryLink();
                content = programs.get(0).getHistory();
            } else if (context.getString(R.string.program_mission_vision).equals(type)) {
                link = programs.get(0).getMissionVisionLink();
                content = programs.get(0).getMissionVision();
            } else if (context.getString(R.string.curriculum).equals(type)) {
                link = programs.get(0).getCurriculumLink();
                content = programs.get(0).getCurriculum();
            } else if (context.getString(R.string.profiles).equals(type)) {
                link = programs.get(0).getProfilesLink();
                content = programs.get(0).getProfiles();
            } else if (context.getString(R.string.program_contact).equals(type)) {
                link = programs.get(0).getProfilesLink();
                content = programs.get(0).getContact();
            }
        }

        dbController.destroy();

        info[0] = link;
        info[1] = content;
        return info;
    }

    public static ArrayList<Item> getEventCategories(Context context) {

        ArrayList<Item> items = new ArrayList<>();

        EventsSQLiteController dbController = new EventsSQLiteController(context, 1);

        ArrayList<EventCategory> categories = dbController.selectCategory(null, null);

        for (EventCategory category : categories) {
            ArrayList<EventRelation> relations = dbController.selectRelation(
                    new String[]{EventsSQLiteController.relationColumns[1]},
                    EventsSQLiteController.relationColumns[0] + " = ?", new String[]{category.get_ID()});
            String description = context.getString(R.string.category)+": "+category.getAbbreviation()+", "+
                    context.getString(R.string.events)+": "+relations.size();
            Item item = new Item(getColor(), 0, category.getName(), description);
            items.add(item);
        }

        dbController.destroy();

        return items;
    }

}
