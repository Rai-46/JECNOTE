package jp.ac.jec.cm0146.jecnote.models;

public class MenuItems {
    private int id;
    private String menuTitle;
    private int menuImage;

    public int getId() { return id; }

    public MenuItems setId(int id) {
        this.id = id;
        return this;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public MenuItems setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
        return this;
    }

    public int getMenuImage() {
        return menuImage;
    }

    public MenuItems setMenuImage(int menuImage) {
        this.menuImage = menuImage;
        return this;
    }
}
