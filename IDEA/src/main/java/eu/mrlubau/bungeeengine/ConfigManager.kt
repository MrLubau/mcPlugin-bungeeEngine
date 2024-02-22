import eu.mrlubau.bungeeengine.Bungee_engine
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.YamlConfiguration

class ConfigManager(private val plugin: Bungee_engine) {

    private var config: Configuration? = null

    init {
        reloadConfig()
    }

    fun reloadConfig() {
        val configFile = plugin.dataFolder.resolve("config.yml")

        if (!configFile.exists()) {
            plugin.getResourceAsStream("config.yml")?.use {
                plugin.logger.info("Copying default config.yml to plugin data folder.")
                configFile.parentFile.mkdirs()
                configFile.createNewFile()
                it.copyTo(configFile.outputStream())
            }
        }

        config = YamlConfiguration.getProvider(YamlConfiguration::class.java).load(configFile)
    }

    fun getConfig(): Configuration {
        return config ?: throw IllegalStateException("Config not loaded. Call reloadConfig() first.")
    }

    fun getString(key: String, defaultValue: String): String {
        return getConfig().getString(key, defaultValue)
    }
}
