(defproject scratch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.taoensso/carmine "2.20.0-RC1"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [io.pedestal/pedestal.route "0.5.7"]
                 [io.pedestal/pedestal.jetty "0.5.7"]
                 [buddy "2.0.0"]
                 [org.slf4j/slf4j-simple "1.7.28"]]
  :source-paths ["src" "cljs-src"]
  :profiles
  {:dev
   {:dependencies [[org.clojure/clojurescript "1.10.339"]
                   [com.bhauman/figwheel-main "0.2.3"]
                   [com.bhauman/rebel-readline-cljs "0.1.4"]]
   :resource-paths ["target"]
   :clean-targets ^{:protect false} ["target"]}}
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]}
  :repl-options {:init-ns scratch.core}
  :main scratch.core
  :aot :all)

