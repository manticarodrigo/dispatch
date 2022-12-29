(ns common.utils.json)

(def json-scalar-map
  {:serialize identity
   :parseValue identity
   :parseLiteral #(.parse js/JSON %)})
