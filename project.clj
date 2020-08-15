(defproject nifty "0.1.0"
  :description "Some useful data structures"
  :url "https://github.com/huahaiy/nifty"
  :lein-release {:deploy-via :clojars}
  :deploy-repositories [["releases" :clojars]]
  :license {:name "MIT License"
            :url  "https://github.com/huahaiy/nifty/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.10"]]
  :doo {:build "node"
        :paths {:karma "./node_modules/karma/bin/karma"}
        :karma {:config {"browserDisconnectTimeout" 30000
                         "browserNoActivityTimeout" 90000}}}
  :clean-targets  ^{:protect false} [:target-path "out" "target"]
  :cljsbuild {:builds
              {:dev
               {:source-paths ["src" "test" "dev"]
                :compiler     {:output-to      "target/nifty.js"
                               :output-dir     "target"
                               :optimizations  :none
                               :source-map     true
                               :cache-analysis true
                               :checked-arrays :warn
                               :parallel-build true}}
               :node
               {:source-paths ["src" "test"]
                :compiler     {:output-to      "out/node/nifty.js"
                               :output-dir     "out/node"
                               :optimizations  :advanced
                               :main           "nifty.test"
                               :source-map     "out/node/nifty.js.map"
                               :target         :nodejs
                               :cache-analysis true
                               :checked-arrays :warn
                               :parallel-build true}}
               :browser
               {:source-paths ["src" "test"]
                :compiler     {:output-to      "out/browser/nifty.js"
                               :output-dir     "out/browser"
                               :optimizations  :advanced
                               :main           "nifty.test"
                               :source-map     "out/browser/nifty.js.map"
                               :cache-analysis true
                               :checked-arrays :warn
                               :parallel-build true}}}}
  :profiles {:deploy
             {:aot      [#"nifty\.*"]
              :jvm-opts ["-Dclojure.compiler.direct-linking=true"] }
             :dev
             {:dependencies [[org.clojure/clojurescript "1.10.773"
                              :exclusions [org.clojure/core.rrb-vector]]
                             ;;see https://github.com/emezeske/lein-cljsbuild/issues/469
                             [quantum/org.clojure.core.rrb-vector "0.0.12"]
                             [criterium "0.4.6"]
                             [doo "0.1.11"]
                             [org.clojure/test.check "1.1.0"]
                             [cider/piggieback "0.5.0"]]
              :source-paths ["src" "test" "dev"]
              :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}})
