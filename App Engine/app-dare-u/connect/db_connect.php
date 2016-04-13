<?php
    
    /**
     * Imports
     */
    require_once '../constants/config.php';
    
    class DB_Connect {
        
        /**
         * Variables
         */
        private $con;
    
        /**
         * Constructor
         */
        function __construct() {
    
        }
    
        /**
         * Destructor
         */
        function __destruct() {
            //$this->close();
        }
    
        /**
         * Database connection
         */
        public function connect() {
            $this->con = new mysqli(DB_HOST,                            // hostname
                                    DB_USER,                            // username
                                    DB_PASSWORD,                        // password
                                    DB_DATABASE,                        // database
                                    null,                               // port                 
                                    DB_SOCKET                           // socket
                                    );
            return $this->con;
        }
        
        /**
         * Database query
         */
        public function query($query) {
            if ($this->con) {
                return $this->con->query($query);
            } else {
                return false;
            }
        }
        
        /**
         * Returns the number of rows from a mysql query
         */
        public function getNumRows($result) {
            return $result->num_rows;
        }
        
        /**
         * Begins a transaction to multiple query.
         */
        public function beginTransaction() {
            $this->con->autocommit(FALSE);
        }
        
        /**
         * Commit a transaction with multiples queries.
         */
        public function commit() {
            $this->con->commit();
            $this->con->autocommit(TRUE);
        }
        
        /**
         * Undo the latest active transactions.
         */
        public function rollBack() {
            $this->con->rollback();
            $this->con->autocommit(TRUE);
        }
    
        /**
         * Close the database connection
         */
        public function close() {
            $this->con->close();
        }
    
    } 

?>