package uk.co.innoxium.candor.module.generic;

import org.apache.commons.io.FileUtils;
import uk.co.innoxium.candor.Settings;
import uk.co.innoxium.candor.mod.Mod;
import uk.co.innoxium.candor.module.AbstractModInstaller;
import uk.co.innoxium.candor.module.AbstractModule;
import uk.co.innoxium.candor.util.Logger;
import uk.co.innoxium.cybernize.archive.Archive;
import uk.co.innoxium.cybernize.archive.ArchiveBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public class GenericModInstaller extends AbstractModInstaller {

    public GenericModInstaller(AbstractModule module) {

        super(module);
    }

    @Override
    public boolean canInstall(Mod mod) {
        return true;
    }

    // Returns whether the mod was installed or not
    @Override
    public CompletableFuture<Boolean> install(Mod mod) {

        File modDir = this.module.getModsFolder();
        if(!modDir.exists()) modDir.mkdirs();

        if (Settings.modExtract) {

            try {

                Archive archive = new ArchiveBuilder(mod.getFile()).outputDirectory(modDir).build();
                if(archive.extract())
                    return CompletableFuture.completedFuture(true);
//                ZipUtils.unZipIt(mod.getFile().getCanonicalPath(), modDir.getCanonicalPath());
            } catch (IOException exception) {

                exception.printStackTrace();
                return CompletableFuture.failedFuture(exception);
            }
        } else {

            try {

                if (!FileUtils.directoryContains(modDir, mod.getFile())) {

                    FileUtils.copyFileToDirectory(mod.getFile(), modDir);
                    return CompletableFuture.completedFuture(true);
                }
            } catch (IOException exception) {

                exception.printStackTrace();
                return CompletableFuture.completedFuture(true);
            }
        }

        return CompletableFuture.completedFuture(false);
    }

    @Override
    public boolean uninstall(Mod mod) {

        mod.getAssociatedFiles().forEach(element -> {

            File toDelete = new File(module.getModsFolder(), element.getAsString());
            Logger.info("Deleting: " + toDelete.getAbsolutePath());
            FileUtils.deleteQuietly(new File(module.getModsFolder(), element.getAsString()));
        });
        return true;
    }
}
