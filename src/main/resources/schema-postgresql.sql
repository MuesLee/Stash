CREATE OR REPLACE FUNCTION notify_created_item() RETURNS TRIGGER AS $$
    BEGIN
       PERFORM pg_notify('item_creation', TG_TABLE_NAME);
	   RETURN NEW;
    END;
$$ LANGUAGE plpgsql;
					  
					  
CREATE TRIGGER notify_on_item_creation 
    AFTER INSERT ON Items
    FOR EACH ROW EXECUTE PROCEDURE notify_created_item();