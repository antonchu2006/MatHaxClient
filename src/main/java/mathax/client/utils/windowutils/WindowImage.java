package mathax.client.utils.windowutils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class WindowImage {

    public static WindowImage read(final String folder) {

        final String icon16 = "icon16.png";
        final String icon32 = "icon32.png";
        final String icon64 = "icon64.png";
        final String icon128 = "icon128.png";

        final Path pathIcon16 = Paths.get(folder, icon16);
        final Path pathIcon32 = Paths.get(folder, icon32);
        final Path pathIcon64 = Paths.get(folder, icon64);
        final Path pathIcon128 = Paths.get(folder, icon128);

        return new WindowImage(pathIcon16, pathIcon32, pathIcon64, pathIcon128);
    }

    private final Path icon16;
    private final Path icon32;
    private final Path icon64;
    private final Path icon128;

    private WindowImage(final Path icon16, final Path icon32, final Path icon64, final Path icon128){
        this.icon16 = icon16;
        this.icon32 = icon32;
        this.icon64 = icon32;
        this.icon128 = icon128;
    }

    public InputStream readIcon16() {
        try{
            return Files.newInputStream(icon16, StandardOpenOption.READ);
        }catch(final IOException e){
            throw new RuntimeException("[MatHax] Something went wrong!", e);
        }
    }

    public InputStream readIcon32() {
        try{
            return Files.newInputStream(icon32, StandardOpenOption.READ);
        }catch(final IOException e){
            throw new RuntimeException("[MatHax] Something went wrong!", e);
        }
    }

    public InputStream readIcon64() {
        try{
            return Files.newInputStream(icon64, StandardOpenOption.READ);
        }catch(final IOException e){
            throw new RuntimeException("[MatHax] Something went wrong!", e);
        }
    }

    public InputStream readIcon128() {
        try{
            return Files.newInputStream(icon128, StandardOpenOption.READ);
        }catch(final IOException e){
            throw new RuntimeException("[MatHax] Something went wrong!", e);
        }
    }
}
