package eu.mrlubau.bungeeengine

import ConfigManager
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import java.sql.DriverManager
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import net.md_5.bungee.api.plugin.Listener;

class Bungee_engine : Plugin() {

    private lateinit var configManager: ConfigManager

    private lateinit var dbchost: String
    private lateinit var dbcusername: String
    private lateinit var dbcdatabase: String
    private lateinit var dbcpassword: String

    private lateinit var databaseUrl: String
    private lateinit var databaseUser: String
    private lateinit var databasePassword: String

    override fun onEnable() {
        configManager = ConfigManager(this)

        dbchost = configManager.getString("database.host", "Error")
        dbcusername = configManager.getString("database.username", "Error")
        dbcdatabase = configManager.getString("database.database", "Error")
        dbcpassword = configManager.getString("database.password", "Error")

        databaseUrl = "jdbc:mysql://$dbchost/$dbcdatabase"
        databaseUser = dbcusername
        databasePassword = dbcpassword

        val scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate({
            processRequests()
        }, 0, 1, TimeUnit.SECONDS)
    }


    private fun processRequests() {
        DriverManager.getConnection(databaseUrl, databaseUser, databasePassword).use { connection ->
            val statement = connection.prepareStatement("SELECT * FROM request")
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val username = resultSet.getString("username")
                val server = resultSet.getString("server")
                val minigame = resultSet.getString("minigame") ?: ""

                val command = "send $username $server"
                proxy.pluginManager.dispatchCommand(proxy.console, command)


                val deleteStatement = connection.prepareStatement("DELETE FROM request WHERE username = ?")
                deleteStatement.setString(1, username)
                deleteStatement.executeUpdate()
            }
        }
    }
}
