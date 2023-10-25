#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as string])

(prn (cli/parse-args *command-line-args*))

(def input {:args ["aiff" "example/maiff" "jpeg" "example/jpeg"]
            :opts {:fromdirectory "example"}})

(def ext->folder "Map from file extension to location."
  (apply hash-map (:args input)))

(defn move [from-dir]
  (for [file (fs/list-dir from-dir)
        :let [folder (ext->folder (fs/extension file))]
        :when (and folder
                   (not (fs/exists?
                         (fs/path folder (fs/file-name file)))))]
    (do
      (fs/create-dirs folder)
      (prn (format "%s -> %s" file folder))
      (fs/copy file folder))))

(move (:fromdirectory (:opts input)))
