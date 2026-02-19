package me.kieran.kjcontrol;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class KJControlTest {

    ServerMock mock;
    KJControl plugin;

    @BeforeEach
    void setUp() {
        mock = MockBukkit.mock();
        plugin = MockBukkit.load(KJControl.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void pluginEnablesSuccessfully() {
        assertTrue(plugin.isEnabled());
    }

    @Test
    void instanceIsSetOnEnable() {
        assertNotNull(KJControl.getInstance());
        assertEquals(plugin, KJControl.getInstance());
    }

    @Test
    void configLoadsOnEnable() {
        File dataFolder = plugin.getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        assertTrue(configFile.exists());
    }

}