package config

import (
	"github.com/spf13/viper"
)

type Config struct {
	Server   ServerConfig
	Database DatabaseConfig
}

type ServerConfig struct {
	Port string
}

type DatabaseConfig struct {
	Host     string
	Port     string
	User     string
	Password string
	DBName   string
	SSLMode  string
}

func Load() (*Config, error) {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath("./config")
	viper.AddConfigPath(".")

	viper.AutomaticEnv()
	viper.SetEnvPrefix("APP")

	viper.BindEnv("server.port", "APP_SERVER_PORT")
	viper.BindEnv("database.host", "APP_DATABASE_HOST")
	viper.BindEnv("database.port", "APP_DATABASE_PORT")
	viper.BindEnv("database.user", "APP_DATABASE_USER")
	viper.BindEnv("database.password", "APP_DATABASE_PASSWORD")
	viper.BindEnv("database.dbname", "APP_DATABASE_DBNAME")
	viper.BindEnv("database.sslmode", "APP_DATABASE_SSLMODE")

	if err := viper.ReadInConfig(); err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
			return nil, err
		}
	}

	var config Config
	if err := viper.Unmarshal(&config); err != nil {
		return nil, err
	}

	return &config, nil
}
