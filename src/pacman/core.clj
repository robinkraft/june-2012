(ns pacman.core
  (:use [incanter core processing]
        clojure.pprint))

;; pacman vars
(def X (ref nil))
(def Y (ref nil))
(def nX (ref nil))        
(def nY (ref nil))        

;; ghost vars
(def gX (ref nil))
(def gY (ref nil))
(def gnX (ref nil))        
(def gnY (ref nil))

;; elipse details
(def radius (ref 50))

(defn valid-key?
  [key-code]
  (let [valid-keys #{UP DOWN LEFT RIGHT}]
    (contains? valid-keys key-code)))

(defn chg-pos
  "Update Pacman's location on keypress"
  [key-event]
  (let [code (.getKeyCode key-event)]
    (dosync
     ;; if valid keystroke, assign ghost location to current pacman location
     (if (valid-key? code)
       [(ref-set gnX @nX) (ref-set gnY @nY)])
     
     ;; reassign pacman location based on keyboard input
     (cond (= LEFT code) (ref-set nX (- 10 @nX))
           (= RIGHT code) (ref-set nX (+ 10 @nX))
           (= UP code) (ref-set nY (- 10 @nY))
           (= DOWN code) (ref-set nY (+ 10 @nY))))))


(defn draw-circle []
  (let [delay 20 ;; delay for moving ghost after keypress
        sktch (sketch
               (setup []
                      (doto this
                        (size 50 50) ;; initial size of circle
                        (stroke-weight 2) ;; weight of border of circle
                        (framerate 15)
                        smooth)
                      (dosync ;; initial state of pacman and ghost
                       (ref-set X (/ (width this) 2))
                       (ref-set Y (/ (width this) 2))
                       (ref-set nX @X)
                       (ref-set nY @Y)
                       (ref-set gX (+ (width this) @radius))
                       (ref-set gY (+ (height this) @radius))
                       (ref-set gnX @gX)
                       (ref-set gnY @gY)))
               
               (draw []
                     (dosync
                      (ref-set radius (+ @radius (sin (/ (frame-count this) 4))))
                      (ref-set X (+ @X (/ (- @nX @X) 1)))
                      (ref-set Y (+ @Y (/ (- @nY @Y) 1)))
                      (ref-set gX (+ @gX (/ (- @gnX @gX) delay)))
                      (ref-set gY (+ @gY (/ (- @gnY @gY) delay))))
   
                    (doto this
                      (background 125) ;; gray
                      (fill 0 121 184)
                      (stroke 255)
                      (ellipse @X @Y @radius @radius)
                      (ellipse @gX @gY @radius @radius)
                      ;;(save "proc_example1.png")
                      ))
               (keyPressed ;; part of incanter.processing api
                    [key-event]
                    (chg-pos key-event)))]
    (view sktch :size [200 200])))

(defn -main [& args]
  (draw-circle))