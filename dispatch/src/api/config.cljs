(ns api.config)

(def STAGE (or js/process.env.STAGE "dev"))
(def SALT (or js/process.env.SALT "$2a$04$9pk37pctRggG8IEEn8oBf.Q/UrxA07gut3e0UiH9JKHSpf2YaLbOO"))
