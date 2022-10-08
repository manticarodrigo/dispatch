(ns config)

(def APP_ENV (or js/process.env.APP_ENV "dev"))
(def APP_SALT (or js/process.env.APP_SALT "$2a$04$9pk37pctRggG8IEEn8oBf.Q/UrxA07gut3e0UiH9JKHSpf2YaLbOO"))

(def PGHOST js/process.env.PGHOST)
(def PGDATABASE js/process.env.PGDATABASE)
(def PGPORT js/process.env.PGPORT)
(def PGPASSWORD js/process.env.PGPASSWORD)
(def PGUSER js/process.env.PGUSER)
