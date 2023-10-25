#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as string])

(def spec
  {:mappings
   {:coerce []
    :alias :m
    :desc "List of file extensions and folders. \nEach file with a specified extension will be moved to the related folder. List must be of even length.\n Example: --mappings jpg jpgfolder png pngfolder\n"
    :validate {:pred #(even? (count %))
               :ex-msg (fn [{:keys [value]}]
                         (str "Each file extension needs to have a folder to map to. You provided " value " which should be of even length, but is not."))}}

   :fromdirectory
   {:desc "Directory containing all files that should be moved\n"
    :alias :d
    :default-desc (str "Default is current directory: " (fs/cwd) "\n")
    :default (str (fs/cwd))}

   :help
   {:desc "Get help"
    :alias :h
    :default false}})

(def example-input ["--mappings" "aiff" "example/maiff" "jpeg" "example/jpe"
                    "--fromdirectory" "example"])

(def input (cli/parse-opts (or *command-line-args* example-input)
                           {:spec spec}))

(when (:help input)
  (println "This program helps you organize your files by moving them based on file extension and size.\n")
  (println (cli/format-opts {:spec spec}))
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
