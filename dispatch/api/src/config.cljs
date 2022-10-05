(ns config)

(def APP_ENV (or js/process.env.APP_ENV "dev"))
(def BASE_URL (or js/process.env.BASE_URL "http://localhost:3000"))

(def PGHOST js/process.env.PGHOST)
(def PGDATABASE js/process.env.PGDATABASE)
(def PGPORT js/process.env.PGPORT)
(def PGPASSWORD js/process.env.PGPASSWORD)
(def PGUSER js/process.env.PGUSER)
