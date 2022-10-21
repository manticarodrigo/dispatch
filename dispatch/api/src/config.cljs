(ns config)

(def STAGE (or js/process.env.STAGE "dev"))
(def SALT (or js/process.env.SALT "$2a$04$9pk37pctRggG8IEEn8oBf.Q/UrxA07gut3e0UiH9JKHSpf2YaLbOO"))

(def PGHOST js/process.env.PGHOST)
(def PGDATABASE js/process.env.PGDATABASE)
(def PGPORT js/process.env.PGPORT)
(def PGUSER js/process.env.PGUSER)
(def PGPASSWORD js/process.env.PGPASSWORD)
