(ns api.config)

(def STAGE (or js/process.env.STAGE "local"))
(def VERSION (or js/process.env.VERSION ""))
(def SALT (or js/process.env.SALT "$2a$04$9pk37pctRggG8IEEn8oBf.Q/UrxA07gut3e0UiH9JKHSpf2YaLbOO"))
(def SITE_BUCKET_NAME (or js/process.env.SITE_BUCKET_NAME ""))
