import os
import json
from com.chaquo.python import Python


class FlagChecker:
    def __init__(self, filename="bot_stop.flag"):
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        self.flag_path = os.path.join(files_path, filename)

    def is_flag_present(self):
        return os.path.exists(self.flag_path)

    def get_flag_path(self):
        return self.flag_path
        

class ConfigManager:
    def __init__(self, filename="config.json"):
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        self.path = os.path.join(files_path, filename)

        if not os.path.exists(self.path):
            self.save({})  # Create empty config file

    def load(self):
        if not os.path.exists(self.path):
            return {}
        with open(self.path, "r", encoding="utf-8") as f:
            return json.load(f)

    def save(self, data):
        with open(self.path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=4)

    def update(self, new_data):
        config = self.load()
        config.update(new_data)
        self.save(config)

    def reset(self):
        self.save({})

# CUSTOM COMMANDS

class CustomCommandsManager:
    def __init__(self, filename="custom_cmd.json"):
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        self.path = os.path.join(files_path, filename)
        if not os.path.exists(self.path):
            self.save({})

    def load(self):
        if not os.path.exists(self.path):
            return {}
        try:
            with open(self.path, "r", encoding="utf-8") as f:
                return json.load(f)
        except json.JSONDecodeError:
            print("[⚠️] JSON Decode Error in custom commands file. Resetting...")
            self.reset()
            return {}

    def save(self, data):
        if not self.is_valid_config(data):
            raise ValueError("Invalid config format: Expected a dictionary with command names as keys.")
        with open(self.path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=4)

    def update(self, new_data):
        if not self.is_valid_config(new_data):
            raise ValueError("New data must be a dictionary of commands.")
        config = self.load()
        config.update(new_data)
        self.save(config)

    def reset(self):
        self.save({})

    def delete_command(self, cmd_name):
        config = self.load()
        if cmd_name in config:
            del config[cmd_name]
            self.save(config)
            return True
        return False

    def get_command(self, cmd_name):
        config = self.load()
        return config.get(cmd_name)

    def list_commands(self):
        return list(self.load().keys())

    def is_valid_config(self, config):
        return isinstance(config, dict) and all(isinstance(v, dict) for v in config.values())


# --- COMMAND LOG SYSTEM ---

class CommandLogManager:
    def __init__(self, filename="cmd_logs.json"):
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        self.path = os.path.join(files_path, filename)
        if not os.path.exists(self.path):
            self.save([])  # Create empty list if not exists

    def load(self):
        if not os.path.exists(self.path):
            return []
        with open(self.path, "r", encoding="utf-8") as f:
            return json.load(f)

    def save(self, logs):
        with open(self.path, "w", encoding="utf-8") as f:
            json.dump(logs, f, indent=4)

    def add_log(self, command, username, server, channel, datetime):
        logs = self.load()
        new_log = {
            "command": command,
            "username": username,
            "server": server,
            "channel": channel,
            "datetime": datetime
        }
        logs.insert(0, new_log)  # Add on top
        self.save(logs)

    def delete_log(self, index):
        logs = self.load()
        if 0 <= index < len(logs):
            logs.pop(index)
            self.save(logs)

    def clear_logs(self):
        self.save([])

# --- CONSOLE LOG SYSTEM ---

class ConsoleLogManager:
    def __init__(self, filename="console_logs.txt"):
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        self.path = os.path.join(files_path, filename)

    def add_console_log(self, message):
        with open(self.path, "a", encoding="utf-8") as f:
            f.write(message + "\n")

    def save_console_logs(self, logs):
        with open(self.path, "w", encoding="utf-8") as f:
            f.write(logs)

    def get_console_logs(self):
        if not os.path.exists(self.path):
            return ""
        with open(self.path, "r", encoding="utf-8") as f:
            return f.read()

    def clear_console_logs(self):
        with open(self.path, "w", encoding="utf-8") as f:
            f.write("")


class RonDB:
    def __init__(self, db_name="default_db.json"):
        """
        Initialize a database with the specified name.
        Automatically creates database folder and file if they don't exist.
        
        Args:
            db_name (str): Name of the database file (e.g., "database1.json")
        """
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        self.db_dir = os.path.join(files_path, "database")
        
        # Create database directory if it doesn't exist
        if not os.path.exists(self.db_dir):
            os.makedirs(self.db_dir)
            
        # Ensure filename ends with .json
        if not db_name.endswith('.json'):
            db_name += '.json'
            
        self.path = os.path.join(self.db_dir, db_name)
        
        # Initialize with empty database if file doesn't exist
        if not os.path.exists(self.path):
            self.save({})

    def load(self):
        """Load entire database content"""
        if not os.path.exists(self.path):
            return {}
        with open(self.path, "r", encoding="utf-8") as f:
            return json.load(f)

    def save(self, data):
        """Save entire database"""
        with open(self.path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent=4, ensure_ascii=False)

    def get(self, key, default=None):
        """Get value by key"""
        db = self.load()
        return db.get(key, default)

    def set(self, key, value):
        """Set value for key"""
        db = self.load()
        db[key] = value
        self.save(db)
        return True

    def delete(self, key):
        """Delete key from database"""
        db = self.load()
        if key in db:
            del db[key]
            self.save(db)
            return True
        return False

    def update(self, key, updater):
        """
        Update value for key using a function
        Example: db.update("user1", lambda val: {**val, "score": val["score"]+1})
        """
        db = self.load()
        if key in db:
            db[key] = updater(db[key])
            self.save(db)
            return True
        return False

    def exists(self, key):
        """Check if key exists"""
        return key in self.load()

    def clear(self):
        """Clear entire database (keeps the file)"""
        self.save({})

    def get_all(self):
        """Get all key-value pairs"""
        return self.load()

    def get_or_create(self, key, default_value):
        """Get value or create if doesn't exist"""
        if not self.exists(key):
            self.set(key, default_value)
        return self.get(key)

    @classmethod
    def list_databases(cls):
        """List all available database files in the database folder"""
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        db_dir = os.path.join(files_path, "database")
        
        if not os.path.exists(db_dir):
            return []
            
        return sorted([f for f in os.listdir(db_dir) if f.endswith('.json')])

    @classmethod
    def delete_database(cls, db_name):
        """Completely delete a database file"""
        if not db_name.endswith('.json'):
            db_name += '.json'
            
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        db_path = os.path.join(files_path, "database", db_name)
        
        if os.path.exists(db_path):
            os.remove(db_path)
            return True
        return False

    @classmethod
    def database_exists(cls, db_name):
        """Check if a database file exists"""
        if not db_name.endswith('.json'):
            db_name += '.json'
            
        context = Python.getPlatform().getApplication().getApplicationContext()
        files_path = context.getFilesDir().getAbsolutePath()
        db_path = os.path.join(files_path, "database", db_name)
        
        return os.path.exists(db_path)