<?php

    class JSON_Converter {
        
        /**
         * Constructor
         */
        function __construct() { 
        }
        
        /**
         * Destructor
         */
        function __destruct() {            
        }
        
        /**
         * Returns a mySQL result converted to array
         */
        public function queryToArray($result) {
            $array = array();
            while ($row = $result->fetch_object()) {
                $array[] = $row;
            }
            return $array;
        }
        
    }
	
?>