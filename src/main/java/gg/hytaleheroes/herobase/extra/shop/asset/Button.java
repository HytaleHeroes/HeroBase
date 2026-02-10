package gg.hytaleheroes.herobase.extra.shop.asset;

import java.util.List;

public final class Button {
    public final String title;
    public final List<String> commmands;
    public final boolean openChooser;


    Button(String title, List<String> commmands, boolean openChooser) {
        this.title = title;
        this.commmands = commmands;
        this.openChooser = openChooser;
    }
}
