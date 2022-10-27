(ns api.config)

(def STAGE (or js/process.env.STAGE "dev"))
(def SALT (or js/process.env.SALT "$2a$04$9pk37pctRggG8IEEn8oBf.Q/UrxA07gut3e0UiH9JKHSpf2YaLbOO"))

(def DB_HOST js/process.env.PGHOST)
(def DB_NAME js/process.env.PGDATABASE)
(def DB_PORT js/process.env.PGPORT)
(def DB_USER js/process.env.PGUSER)
(def DB_PASSWORD js/process.env.PGPASSWORD)
