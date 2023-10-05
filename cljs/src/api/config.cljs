(ns api.config)

(def STAGE (or js/process.env.STAGE "local"))
(def VERSION (or js/process.env.VERSION ""))
(def SALT (or js/process.env.SALT "$2a$04$9pk37pctRggG8IEEn8oBf.Q/UrxA07gut3e0UiH9JKHSpf2YaLbOO"))
(def SITE_BUCKET_NAME (or js/process.env.SITE_BUCKET_NAME ""))
(def GOOGLE_API_KEY (or js/process.env.GOOGLE_API_KEY "AIzaSyDk7JiOgLO9FWGRkAY9IYhXkO7ia-Oz-6A"))
(def STRIPE_SECRET_KEY (or js/process.env.STRIPE_SECRET_KEY "sk_test_51Lw6AaJtPsEN2nVPC06oovDTvJYxvdqVyXRAu1EkeZ38eyFJERJBlvmCAAh9SaOj4lV69BFZPyxQpzXrTztdy6ML00M2IeY6lA"))
(def RESEND_API_KEY (or js/process.env.RESEND_API_KEY ""))
