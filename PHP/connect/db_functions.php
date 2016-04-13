<?php
    
    /**
     * Imports
     */
    require_once 'db_connect.php';
    require_once '../utils/json_converter.php';
    require_once '../utils/security.php';
    require_once '../constants/errors.php';
    
    class DB_Functions {
        
        /**
         * Variables
         */
        private $db;
        private $json;
        private $sec;
        
        /**
         * Constructor
         */
        function __construct() {
            $this->db = new DB_Connect();
            $this->db->connect();
            $this->json = new JSON_Converter();
            $this->sec = new Security();
        }
    
        /**
         * Destructor
         */
        function __destruct() {
            $this->db->close();
        }
        
        /**
         * Close the connection
         */
        public function close() {
            $this->db->close();
        }
        
        /**
         * Database query
         */
        public function query($query) {
            return $this->db->query($query);
        }
        
        /**
         * Method to create a new user.
         */
        public function createUser($username, $password, $email, $birthyear, $country, $language, $device, $IMEI, $notification) {
            if (!$this->sec->isValidASCII($username) || !$this->sec->isValidASCII($email)) { // validate correct form
                return ERROR_FORM;
            } 
            $password = $this->sec->firstStep($password, $username); // desencrypt the password
            if (!$this->sec->isValidASCII($password)) { // validate correct form
                return ERROR_FORM;
            } 
            $password = $this->sec->secondStep($password, $username); // Encrypt password
            
            $result = $this->db->query("select id from user where username=('$username')");
            
            if ($result) {
                if ($this->db->getNumRows($result) == 0) {
                    $result = $this->db->query("insert into user (username, password, email, birthyear, country, language, device, IMEI, notification) 
                                values ('$username', '$password', '$email', '$birthyear', '$country', '$language', '$device', '$IMEI', '$notification')");
                    if ($result) {
                        return $this->validateUser($username, $password, false);
                    } else {
                        return ERROR_CON;
                    }
                } else {
                    return ERROR_SIGNUP;
                }
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns an user
         */
        public function validateUser($username, $password, $encrypt, $device, $imei, $notification, $bool) {
            if (!$this->sec->isValidASCII($username)) { // validate correct form
                return ERROR_FORM;
            }
            
            if ($encrypt) { 
                $password = $this->sec->firstStep($password, $username); // desencrypt the password
                if (!$this->sec->isValidASCII($password)) { // validate correct form
                    return ERROR_FORM;
                } 
                $password = $this->sec->secondStep($password, $username); // Encrypt password
            }
            
            if ($bool) {
                $sql = "update user set device=('$device'), IMEI=('$imei'), notification=('$notification') where username=('$username') and password=('$password')";
                $this->db->query($sql);
            }
            
            $result = $this->db->query("select * from user where username=('$username') and password=('$password')");
            if ($result) {
                if ($this->db->getNumRows($result) > 0) {
                    $resultArray = $this->json->queryToArray($result);
                    return json_encode($resultArray);
                } else {
                    return ERROR_LOGIN;
                }
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Close the user session and delete the device information about the user.
         */
        public function closeSession($userId) {
            $result = $this->db->query("update user set device=(''), IMEI=(''), notification=('') where id=$userId");
            if ($result) {
                return ERROR_OK;
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Method to create a new contact.
         */
        public function createContact($userId, $contactId, $favorite) {
            if (!$this->isContact($userId, $contactId)) {
                $result = $this->db->query("insert into contact (userId, contactId, favorite) values ($userId, $contactId, $favorite)");
                if ($result) {
                    return ERROR_OK;
                } else {
                    return ERROR_CON;
                }
            } else {
                return ERROR_CONTACT;
            }
        }
        
        /**
         * Returns a boolean that indicates if exist a contact between user and contact.
         */
        public function isContact($userId, $contactId) {
            $result = $this->db->query("select * from contact where userId=$userId and contactId=$contactId");
            if ($result) {
                return ($this->db->getNumRows($result) > 0);
            } else {
                return true;
            }
        }
        
        /**
         * Returns the contacts with a coincidence with the given letters.
         */
        public function searchContacts($userId, $toSearch) {
            $result = $this->db->query("select id, username, restrictions, device, notification from user where id<>($userId) and username like ('$toSearch%') order by username");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Update or delete a contact and returns all the contacts.
         */
        public function updateContact($userId, $contactId, $favorite=0, $del=false) {
            $result = false;
            if ($del) {
                $result = $this->db->query("delete from contact where userId=$userId and contactId=$contactId");
            } else {
                $result = $this->db->query("update contact set favorite=$favorite where userId=$userId and contactId=$contactId");
            }
            if ($result) {
                return $this->getContacts($userId);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns all the contacts from an user.
         */
        public function getContacts($userId) {
            $result = $this->db->query("select distinct u.id, u.username, u.restrictions, u.device, u.notification, c.favorite from user u, contact c 
                        where u.id in (select contactId from contact where userId=$userId) and c.contactId=u.id and c.userId=$userId order by c.favorite desc, u.username");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Method to create a new challenge.
         */
        public function createChallenge($userId, $username, $contactId, $contact, $challenge) {
            if ($this->isChallenge($userId, $contactId)) return ERROR_CHALLENGE;
            
            // Contact challenge
            $id = strval($contactId)."/".strval($userId);
            $result = $this->db->query("insert into challenge (id, userId, username, contactId, contact, challenge, turn) 
                                        values ('$id', $contactId, '$contact', $userId, '$username', '$challenge', 1)");
            
            if ($result) {
                return ERROR_OK;
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns a boolean that indicates if exist a challenge created between user and contact.
         */
        public function isChallenge($userId, $contactId) {
            $result = $this->db->query("select * from challenge where userId=$contactId and contactId=$userId");
            if ($result) {
                return ($this->db->getNumRows($result) > 0);
            } else {
                return true;
            }
        }
        
        /**
         * Updates the challenge from the user.
         * The 'id' is composed by str(userId) + "/" + str(contactId)
         */
        public function updateChallenge($userId, $contactId, $type, $file) {
            $id = strval($userId)."/".strval($contactId);
            $result = $this->db->query("update challenge set type=('$type'), url=('$file'), turn=0 where id=('$id')");
            if ($result) {
                return ERROR_OK;
            }
        }
        
        /**
         * Deletes the challenge of the the user and contact given by the parameters.
         */
        public function deleteChallenge($userId, $contactId) {
            $id = strval($userId)."/".strval($contactId);
            $result = $this->db->query("delete from challenge where id=('$id')");
            
            if ($result) {
                return $this->getChallenges($contactId);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns all the challenges from an user.
         */
        public function getChallenges($userId) {
            $result = $this->db->query("select * from challenge where (userId=$userId and turn=1) or contactId=$userId order by turn asc, lastModification desc");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns a single challenge.
         */
        public function getSingleChallenge($userId1, $userId2) {
            $id = strval($userId1)."/".strval($userId2);
            $result = $this->db->query("select * from challenge where id=$id");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns all the post from the contacts of an user.
         */
        public function getPosts($userId) {
            $result = $this->db->query("select * from post where userId in (select contactId from contact where userId=$userId) 
                                        order by lastModification desc, username");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns all the posts from an user.
         */
        public function getUserPosts($userId) {
            $result = $this->db->query("select * from post where userId=($userId)");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Method to post a challenge in your timeline.
         */
        public function createPost($userId, $username, $contactId, $contact, $challenge, $type, $file) {
            $result = $this->db->query("insert into post (userId, username, contactId, contact, challenge, type, url) 
                                        values ($userId, '$username', $contactId, '$contact', '$challenge', '$type', '$file')");
            if ($result) {
                return ERROR_OK;
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Deletes the post given by the parameter.
         */
        public function deletePost($id, $userId) {
            $result = $this->db->query("delete from post where id=($id)");
            
            if ($result) {
                return $this->getUserPosts($userId);
            } else {
                return ERROR_CON;
            }
        }
        
        /**
         * Returns all the users from the database
         */
        public function getAllUsers() {
            $result = $this->db->query("select * from user");
            if ($result) {
                $resultArray = $this->json->queryToArray($result);
                return json_encode($resultArray);
            } else {
                return ERROR_CON;
            }
        }
    
    }

?>