package mr.limpios.smart_divide_backend.infraestructure.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import mr.limpios.smart_divide_backend.domain.models.Card;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;

class CardMapperTest {

    @Test
    void toSchema_mapsCorrectly() {
        Card card = Instancio.create(Card.class);

        CardSchema result = CardMapper.toSchema(card);

        assertNotNull(result);
        assertEquals(card.id(), result.getId());
        assertEquals(card.lastDigits(), result.getLastDigits());
        assertEquals(card.brand(), result.getBrand());
        assertEquals(card.expMonth(), result.getExpMonth());
        assertEquals(card.expYear(), result.getExpYear());
        assertEquals(card.token(), result.getToken());
        assertNull(result.getUser()); 
    }

    @Test
    void toModel_mapsCorrectly() {
        CardSchema schema = Instancio.create(CardSchema.class);

        Card result = CardMapper.toModel(schema);

        assertNotNull(result);
        assertEquals(schema.getId(), result.id());
        assertEquals(schema.getLastDigits(), result.lastDigits());
        assertEquals(schema.getBrand(), result.brand());
        assertEquals(schema.getExpMonth(), result.expMonth());
        assertEquals(schema.getExpYear(), result.expYear());
        assertEquals(schema.getToken(), result.token());
    }
}