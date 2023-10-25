#!/usr/bin/env bb
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as string])

(def spec
  {:spec {:extension_to_folder
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

          :cutoff
          {:desc "Any file smaller than the cutoff (in MB) will be considered a sample. Samples are sent to the folder given in extension_to_folder, but with a -sample suffix, like wav-sample."
           :alias :c
           :default-desc "Default: 3.5"
           :default 3.5}

          :help
          {:desc "Get help"
           :alias :h
           :default false}}})

(def example-input ["-e" "aiff" "/home/sam/aiff" "jpeg" "/home/sam/jpg"
                    "--fromdirectory" "example"])

(def input (cli/parse-opts (or *command-line-args* example-input)
                           spec))

(when (:help input)
  (println "This program helps you organize your files by moving them based on file extension and size.\n")
  (println (cli/format-opts spec))
  (System/exit 0))

(def ext->folder-map "Map from file extension to location."
  (apply hash-map (:extension_to_folder input)))

(defn file->folder "Returns the folder for a given file. If the file is smaller than sample-cutoff, the folder will be suffixed with -sample to show that it's not a full song." [file sample-cutoff]
  (let [sample? (> (* 1000000 sample-cutoff) (fs/size file))
        base (ext->folder-map (fs/extension file))]
    (if sample?
      (str base "-sample")
      base)))

(defn move [from-dir sample-cutoff]
  (doall (for [file (fs/list-dir from-dir)
               :let [folder (file->folder file sample-cutoff)
                     new-path (fs/path folder (fs/file-name file))]
               :when (and folder
                          (not (fs/exists? new-path)))]
           (do
             (println (str file) " -> " (str new-path))
             (fs/create-dirs folder)
             (fs/copy file folder)))))

(move (:fromdirectory input) (:cutoff input))
