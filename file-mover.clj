#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as string])

(def cli-options {:help {:coerce :boolean}})

;(def args (cli/parse-args *command-line-args*)) 
(def args (cli/parse-args ["aiff" "example/maiff" "jpg" "example/jpeg"]))

(def file-extension->folder
  (apply hash-map (:args args)))

(defn move [base-dir]
  (for [f (fs/list-dir base-dir)
        :let [extension (fs/extension f)
              folder 
              (file-extension->folder extension)]]
    (do
      (fs/create-dirs folder)
      (prn extension)
      (prn folder)
      (prn (format "%s -> %s" f folder))
      (fs/copy f folder))))

(move "example")
