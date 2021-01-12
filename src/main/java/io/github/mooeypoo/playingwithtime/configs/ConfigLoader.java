package io.github.mooeypoo.playingwithtime.configs;

import java.io.IOException;
import java.nio.file.Path;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;

public class ConfigLoader<C> extends ConfigurationHelper<C> {
	private volatile C configData;
	private String fileName = null;

	private ConfigLoader(Path configFolder, String fileName, ConfigurationFactory<C> factory) {
		super(configFolder, fileName, factory);
		this.fileName = fileName;
	}

	public static <C> ConfigLoader<C> create(Path configFolder, String fileName, Class<C> configClass) {
		// SnakeYaml example
		SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
				.useCommentingWriter(true) // Enables writing YAML comments
				.build();
		return new ConfigLoader<>(configFolder, fileName,
				new SnakeYamlConfigurationFactory<>(configClass, ConfigurationOptions.defaults(), yamlOptions));
	}

	public void reloadConfig() throws ConfigurationException {
		try {
			configData = reloadConfigData();
		} catch (IOException ex) {
			throw new ConfigurationException(
				this.fileName, "The was a problem loading this file.",
				ex
			);
		} catch (ConfigFormatSyntaxException ex) {
			configData = getFactory().loadDefaults();
			throw new ConfigurationException(
				this.fileName, "The yaml syntax of this file is malformed. Using defaults, instead.",
				ex
			);
		} catch (InvalidConfigException ex) {
			configData = getFactory().loadDefaults();
			throw new ConfigurationException(
				this.fileName, "The keys and values used in this file are malformed. Using defaults, instead.",
				ex
			);
		}
	}

	public C getConfigData() throws ConfigurationException {
		C configData = this.configData;
		if (configData == null) {
			throw new ConfigurationException(
				this.fileName, "Configuration file was not yet loaded."
			);
		}
		return configData;
	}
}
