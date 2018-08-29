/**
 * @author Paul Dennis (pd236m)
 * Aug 23, 2018
 */
package paul.TextQuest.misc_unused;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import paul.TextQuest.entities.BackpackItem;

/**
 * This class was an attempt to more cleanly serialize the BackpackItems. 
 * I've marked it as deprecated for the following reasons:
 * 1. Could not get it working (mainly).
 * 2. Realized that some of the efficiencies (for example not serializing
 * an onPickup/eventual onDrop) might actually be bad. For example, might
 * want to be able to create an item with persistent onPickup/onDrop effects
 * (like placing a beacon of some kind). 
 */
@Deprecated
public class BackpackItemSerializer extends StdSerializer<BackpackItem>{
	
	
	public BackpackItemSerializer () {
		this(null);
	}
	
	public BackpackItemSerializer (Class<BackpackItem> t) {
		super(t);
	}

	@Override
	public void serialize(BackpackItem item, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
		
		//jsonGen.writeStartObject();
		jsonGen.writeTypeId(BackpackItem.class);
		jsonGen.writeStringField("name", item.getName());
		if (item.isQuestItem()) {
			jsonGen.writeBooleanField("questItem", true);
		}
		if (item.getValue() != 0) {
			jsonGen.writeNumberField("value", item.getValue());
		}
		//jsonGen.writeEndObject();
	}
	
	@Override
	public void serializeWithType (BackpackItem item, JsonGenerator jsonGen,
			SerializerProvider provider, TypeSerializer typeSerializer) throws IOException {
		typeSerializer.writeTypePrefixForObject(item, jsonGen);
		serialize(item, jsonGen, provider);
		typeSerializer.writeTypeSuffixForObject(item, jsonGen);
	}
}
