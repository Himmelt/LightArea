package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Mod {
    String modid();

    String name() default "";

    String version() default "";

    String dependencies() default "";

    boolean useMetadata() default false;

    String acceptedMinecraftVersions() default "";

    String acceptableRemoteVersions() default "";

    String acceptableSaveVersions() default "";

    String bukkitPlugin() default "";

    String certificateFingerprint() default "";

    String modLanguage() default "java";

    String modLanguageAdapter() default "";

    /**
     * @deprecated
     */
    @Deprecated
    String asmHookClass() default "";

    boolean canBeDeactivated() default false;

    String guiFactory() default "";

    Mod.CustomProperty[] customProperties() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface InstanceFactory {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Metadata {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Instance {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface EventHandler {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface CustomProperty {
        String k();

        String v();
    }
}
