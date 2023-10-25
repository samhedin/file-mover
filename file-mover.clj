#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as string])

(def spec 
  {:mappings
   {:coerce []
    :alias :m
    :desc "List of file extensions and folders. Each file with a specified extension will be moved to the related folder. List must be of even length. Example: --mappings jpg jpgfolder png pngfolder"
    :validate {:pred #(even? (count %))
               :ex-msg (fn [{:keys [value]}]
                         (str "Each file extension needs to have a folder to map to. You provided " value " which should be of even length, but is not."))}}

   :fromdirectory
   {:desc "Directory with all files that should be moved, defaults to current directory"
    :alias :d
    :default (str (fs/cwd))}

   :help
   {:desc "Get help"
    :alias :h
    :default false}})

(def example-input ["--mappings" "aiff" "example/maiff" "jpeg" "example/jpe"
                            "--fromdirectory" "example" "-h"])
(def input (cli/parse-opts *command-line-args*
                           {:spec spec}))

(when (:help input)
  (println (cli/format-opts {:spec spec :order [:mappings :fromdirectory]}))
  (System/exit 0))

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
