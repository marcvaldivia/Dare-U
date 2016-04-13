<?php

    /**
     * Imports
     */
    require_once '../constants/config.php';
    require_once '../constants/urls.php';
    require_once '../constants/preferences.php';
 
    class GCM {
    
        /**
        * Constructor
        */
        function __construct() { 
        }
        
        /**
         * Convert the given data in arrays and send a push notification.
         */
        public function sendNotification($notification, $contact, $message) {
            if (isset($notification)) {
                $registrationId = array($notification);
                $msg = array(MESSAGE => strval($contact).','.strval($message));
                return $this->send($registrationId, $msg);
            } else {
                return false;
            }
        }
    
        /**
        * Sending Push Notification
        */
        private function send($registatoin_ids, $message) {
    
            $fields = array(
                'registration_ids' => $registatoin_ids,
                'data' => $message,
            );
    
            $headers = array(
                'Authorization: key=' . GOOGLE_API_KEY,
                'Content-Type: application/json'
            );
            // Open connection
            $ch = curl_init();
    
            // Set the url, number of POST vars, POST data
            curl_setopt($ch, CURLOPT_URL, GOOGLE_SEND);
    
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    
            // Disabling SSL Certificate support temporarly
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    
            // Execute post
            $result = curl_exec($ch);
            if ($result === FALSE) {
                return false;
            }
    
            // Close connection
            curl_close($ch);
            return $result;
        }
    
    }
 
?>