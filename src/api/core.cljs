(ns api.core (:require [api.lib.apollo :as a]
                       [api.lib.prisma :as p]))

(def ^:export server a/server)
(def ^:export prisma p/prisma)
