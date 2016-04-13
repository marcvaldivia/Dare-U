<?php

    /**
     * Imports
     */
    require_once 'date.php';

    class Security {
        
        /**
         * Variables
         */
        private $mod = 128; // Max range of letters (ASCII)
        
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
         * Validate if the String contains characters with a ASCII value above 128.
         */
        public function isValidASCII($string) {
            $isValid = true;
            for ($i = 0; $i < strlen($string); $i++) {
                if (ord($string[$i]) > (($this->mod)-1) || $string[$i] == "'") $isValid = false;
            }
            return isValid;
        }
        
        /**
         * Desencrypt in a first step the data from the app.
         * Returns the real data from the app.
         */
        public function firstStep($toDecrypt, $len) {
            return $this->simpleDecryption($toDecrypt, 1, strlen($len));
        }
        
        /**
         * Encrypt to the first step.
         * Returns the encrypted data.
         */
        public function firstStepEncrypt($toEncrypt, $len) {
            return $this->simpleEncryption($toEncrypt, 1, strlen($len));
        }
        
        /**
         * Encrypt in a second step the data to store in the data base.
         * Returns the encrypted data.
         */
        public function secondStep($toEncrypt, $len) {
            return md5($toEncrypt);
        }
        
        /**
         * Simple encryption by factor and addition (Cesar encryption)
         */
        public function simpleEncryption($toEncrypt, $factor, $add) {
            $encrypted = '';
            for ($i = 0; $i < strlen($toEncrypt); $i++) {
                $numeric = ((ord($toEncrypt[$i])/**$factor*/)+$add)%$this->mod;
                $encrypted .= chr($numeric);
            }
            return $encrypted;
        }
        
        /**
         * Simple decryption by factor and addition (Cesar encryption)
         */
        public function simpleDecryption($toDecrypt, $factor, $add) {
            $decrypted = '';
            for ($i = 0; $i < strlen($toDecrypt); $i++) {
                $numeric = ((ord($toDecrypt[$i])/**$factor*/)-$add)%$this->mod;
                if ($numeric < 0) {
                    $numeric = $this->mod+$numeric;
                }
                $decrypted .= chr($numeric);
            }
            return $decrypted;
        }
        
        /**
         * Returns a TRUE value if our credentials have a valid access.
         */
        public function checkCredentials($date, $auth) {
            $date = new Date($date);
            // Desencrypt the auth credential
            $auth = $this->simpleDecryption($auth, 1, $date->submmss());
            return substr($auth, 3) == strval($date->addDDMM());
        }
        
    }
	
?>