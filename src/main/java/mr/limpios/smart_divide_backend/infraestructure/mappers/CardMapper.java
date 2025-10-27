package mr.limpios.smart_divide_backend.infraestructure.mappers;

import mr.limpios.smart_divide_backend.domain.models.Card;
import mr.limpios.smart_divide_backend.infraestructure.schemas.CardSchema;

public class CardMapper {

  private CardMapper() {}

  public static CardSchema toSchema(Card card) {
    return new CardSchema(card.id(), card.lastDigits(), card.brand(), card.expMonth(),
        card.expYear(), card.token(), null);
  }

  public static Card toModel(CardSchema cardSchema) {
    return new Card(cardSchema.getId(), cardSchema.getLastDigits(), cardSchema.getBrand(),
        cardSchema.getExpMonth(), cardSchema.getExpYear(), cardSchema.getToken());
  }
}
