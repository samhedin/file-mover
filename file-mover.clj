#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as string])

(def spec
  {:extension_to_folder
   {:coerce []
    :alias :e
    :desc "List of file extensions and folders. \nEach file with a specified extension will be moved to the related folder. List must be of even length.\n Example: -e jpg jpgfolder png pngfolder\n"
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

(def example-input ["-e" "aiff" "/home/sam/aiff" "jpeg" "/home/sam/jpg"
                    "--fromdirectory" "example"])

(def input (cli/parse-opts (or *command-line-args* example-input)
                           {:spec spec}))

(when (:help input)
  (println "This program helps you organize your files by moving them based on file extension and size.\n")
  (println (cli/format-opts {:spec spec}))
  (System/exit 0))

(def ext->folder "Map from file extension to location."
  (apply hash-map (:extension_to_folder input)))

(defn move [from-dir]
  (doall (for [file (fs/list-dir from-dir)
               :let [folder (ext->folder (fs/extension file))
                     new-path (fs/path folder (fs/file-name file))]
               :when (and folder
                          (not (fs/exists? new-path)))]
           (do
             (println (str file) " -> " (str new-path))
             (fs/create-dirs folder)
             (fs/copy file folder)))))

(move (:fromdirectory input))
