<?php

    class Date {
        
        /**
         * Variables
         */
        private $year;
        private $month;
        private $day;
        private $hour;
        private $minute;
        private $second;
        private $isValid;
        
        /**
         * Constructor
         */
        function __construct($date) { 
            // Date format: DD/MM/YYYY hh:mm:ss
            if (strlen($date) == 19) {
                $this->year = substr($date, 6, 4);
                $this->month = substr($date, 3, 2);
                $this->day = substr($date, 0, 2);
                $this->hour = substr($date, 11, 2);
                $this->minute = substr($date, 14, 2);
                $this->second = substr($date, 17, 2);
                $this->isValid = true;
            } else {
                $this->isValid = false;
            }
        }
        
        /**
         * Destructor
         */
        function __destruct() {            
        }
        
        public function __toString() {
            return $this->day."/".$this->month."/".$this->year." ".$this->day.":".$this->month;
        }
        
        /**
         * Returns TRUE if the date format is valid.
         */
        public function isValid() {
            return $this->isValid;
        }
        
        /**
         * Returns the addition of the day and the month.
         */
        public function addDDMM() {
            return $this->day + $this->month;
        }
        
        /**
         * Returns the absolute substract of the minute and the second
         */
        public function submmss() {
            $sub = $this->minute - $this->second;
            return abs($sub);
        }
        
    }
	
?>