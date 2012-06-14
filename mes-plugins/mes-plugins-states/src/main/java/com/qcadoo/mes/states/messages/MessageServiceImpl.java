package com.qcadoo.mes.states.messages;

import static com.qcadoo.mes.states.constants.StatesConstants.MODEL_MESSAGE;
import static com.qcadoo.mes.states.constants.StatesConstants.PLUGIN_IDENTIFIER;
import static com.qcadoo.mes.states.messages.constants.StateMessageType.VALIDATION_ERROR;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.qcadoo.mes.states.StateChangeContext;
import com.qcadoo.mes.states.messages.constants.MessageFields;
import com.qcadoo.mes.states.messages.constants.StateMessageType;
import com.qcadoo.mes.states.messages.util.MessagesUtil;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Override
    public final Entity createMessage(final String translationKey, final StateMessageType type, final String correspondField,
            final String... translationArgs) {
        Entity message = getDataDefinition().create();
        message.setField(MessageFields.TYPE, type.getStringValue());
        message.setField(MessageFields.TRANSLATION_KEY, translationKey);
        message.setField(MessageFields.TRANSLATION_ARGS, MessagesUtil.joinArgs(translationArgs));
        message.setField(MessageFields.CORRESPOND_FIELD_NAME, correspondField);
        return message;
    }

    @Override
    public boolean messageAlreadyExists(final Entity message) {
        final SearchCriteriaBuilder criteriaBuilder = getDataDefinition().find();
        criteriaBuilder.add(SearchRestrictions.allEq(message.getFields()));
        final SearchResult result = criteriaBuilder.list();
        return result.getTotalNumberOfEntities() > 0;
    }

    protected DataDefinition getDataDefinition() {
        return dataDefinitionService.get(PLUGIN_IDENTIFIER, MODEL_MESSAGE);
    }

    @Override
    public final void addMessage(final StateChangeContext stateChangeContext, final StateMessageType type,
            final String correspondFieldName, final String translationKey, final String... translationArgs) {
        final Entity message = createMessage(translationKey, type, correspondFieldName, translationArgs);
        addMessage(stateChangeContext, message);
    }

    @Override
    public final void addValidationError(final StateChangeContext stateChangeContext, final String correspondField,
            final String translationKey, final String... translationArgs) {
        addMessage(stateChangeContext, VALIDATION_ERROR, correspondField, translationKey, translationArgs);
    }

    @Override
    public final void addMessage(final StateChangeContext stateChangeContext, final Entity message) {
        final List<Entity> messages = Lists.newArrayList(stateChangeContext.getAllMessages());
        messages.add(message);
        final String messagesFieldName = stateChangeContext.getDescriber().getMessagesFieldName();
        stateChangeContext.setField(messagesFieldName, messages);
        stateChangeContext.save();
    }
}
