import com.tss.FileTranslatorCopier;
import com.tss.OutputCleaner;
import org.junit.jupiter.api.Test;

public class AppRunner {

    @Test
    public void convertFiles() {
        new FileTranslatorCopier().run();
    }

    @Test
    public void cleanupOutput() {
        new OutputCleaner().cleanOutputFolder();
    }
}
