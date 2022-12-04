package jp.ac.jec.cm0146.jecnote.models;

public class MenuItems {
    private String menuTitle;
    private int menuImage;

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
