

### About
Iran Top 5 iranian recommendation website



### Installation


1. Navigate to the root directory (Top5Site) containing the manage.py file in a command line.


2. Install dependencies

      ```
       $ pip install -r requirements.txt
      ```

3.  Create the migrations (generate the SQL commands).

    ```
    $ python manage.py makemigrations
    ```

 
4. Run the migrate command in a shell to create the database tables automatically.
      ```
       $ python manage.py migrate 
      ``` 
   
   
5. Execute the runserver command in a shell to start the development server.This will enable you to access
   the web application in a browser.

      ```
        $ python manage.py runserver
      ```

      or

      ```
      $ python manage.py runserver 0.0.0.0:8000
      ```
   
6. Copy the following url in a browser
   
   http://localhost:8000/
   
   NB: The development server runs on port 8000 by default  

