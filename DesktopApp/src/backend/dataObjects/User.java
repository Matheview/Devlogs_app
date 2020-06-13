package backend.dataObjects;

import java.util.List;

public class User {

    private int id;
    private int user_id;
    private String privilege;
    private String shortcut;
    private String user;
    private String name;

    /**
     * Metoda służaca do wyszukiwania użytkownika na liście
     * @param id id szukanego użytkownika
     * @param list lista z użytkownikami
     * @return zwraca obiekt znalezionega użytkownika lub null w przypadku jego nie znalezienia
     */
    public static User getUserFromListById(Integer id, List<User> list) {
        if (list != null) {
            if (id == null)
                return null;

            for (User user : list) {
                if (user.getId() == id) {
                    return user;
                }
            }
        }
        return null;
    }

    public User(int id, String privilege, String shortcut, String user) {
        this.id = id;
        this.privilege = privilege;
        this.shortcut = shortcut;
        this.user = user;
    }

    public int getId() {
        if (id != 0)
            return id;
        return user_id;
    }

    public void setId(int id) {
        this.id = id;
        this.user_id = id;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public String getName() {
        if (name != null)
            return name;
        return user;
    }

    public void setName(String name) {
        this.name = name;
        this.user = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
