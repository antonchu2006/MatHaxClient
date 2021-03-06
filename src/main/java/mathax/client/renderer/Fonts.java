package mathax.client.renderer;

import mathax.client.MatHaxClient;
import mathax.client.gui.WidgetScreen;
import mathax.client.renderer.text.CustomTextRenderer;
import mathax.client.systems.config.Config;
import mathax.client.utils.files.StreamUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static mathax.client.utils.Utils.mc;

public class Fonts {
    private static final String[] BUILTIN_FONTS = { "Minecraft.ttf", "JetBrains Mono.ttf", "Comfortaa.ttf", "Tw Cen MT.ttf", "Pixelation.ttf" };
    public static final String DEFAULT_FONT = "Comfortaa";
    private static final File FONTS_FOLDER = new File(MatHaxClient.MCCONFIG_FOLDER, "Fonts");

    public static CustomTextRenderer CUSTOM_FONT;

    private static String lastFont = "";

    public static void init() {
        FONTS_FOLDER.mkdirs();

        // Copy built in fonts if they not exist
        for (String font : BUILTIN_FONTS) {
            File file = new File(FONTS_FOLDER, font);
            if (!file.exists()) {
                StreamUtils.copy(Fonts.class.getResourceAsStream("/assets/mathax/fonts/" + font), file);
            }
        }

        // Load default font
        CUSTOM_FONT = new CustomTextRenderer(new File(FONTS_FOLDER, DEFAULT_FONT + ".ttf"));
        lastFont = DEFAULT_FONT;
    }

    public static void load() {
        if (lastFont.equals(Config.get().font)) return;

        File file = new File(FONTS_FOLDER, Config.get().font + ".ttf");
        if (!file.exists()) {
            Config.get().font = DEFAULT_FONT;
            file = new File(FONTS_FOLDER, Config.get().font + ".ttf");
        }

        try {
            CUSTOM_FONT = new CustomTextRenderer(file);
        } catch (Exception ignored) {
            Config.get().font = DEFAULT_FONT;
            file = new File(FONTS_FOLDER, Config.get().font + ".ttf");

            CUSTOM_FONT = new CustomTextRenderer(file);
        }

        if (mc.currentScreen instanceof WidgetScreen && Config.get().customFont) {
            ((WidgetScreen) mc.currentScreen).invalidate();
        }

        lastFont = Config.get().font;
    }

    public static String[] getAvailableFonts() {
        List<String> fonts = new ArrayList<>(4);

        File[] files = FONTS_FOLDER.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                int i = file.getName().lastIndexOf('.');
                if (file.getName().substring(i).equals(".ttf")) {
                    fonts.add(file.getName().substring(0, i));
                }
            }
        }

        return fonts.toArray(new String[0]);
    }
}
