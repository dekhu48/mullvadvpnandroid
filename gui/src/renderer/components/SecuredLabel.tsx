import * as React from 'react';
import styled from 'styled-components';
import { colors } from '../../config.json';
import { messages } from '../../shared/gettext';

export enum SecuredDisplayStyle {
  secured,
  blocked,
  securing,
  unsecured,
  failedToSecure,
}

const securedDisplayStyleColorMap = {
  [SecuredDisplayStyle.securing]: colors.white,
  [SecuredDisplayStyle.secured]: colors.green,
  [SecuredDisplayStyle.blocked]: colors.green,
  [SecuredDisplayStyle.unsecured]: colors.red,
  [SecuredDisplayStyle.failedToSecure]: colors.red,
};

const StyledSecuredLabel = styled.span((props: { displayStyle: SecuredDisplayStyle }) => ({
  color: securedDisplayStyleColorMap[props.displayStyle],
}));

interface ISecuredLabelProps {
  displayStyle: SecuredDisplayStyle;
  className?: string;
}

export default function SecuredLabel(props: ISecuredLabelProps) {
  return <StyledSecuredLabel {...props}>{getLabelText(props.displayStyle)}</StyledSecuredLabel>;
}

function getLabelText(displayStyle: SecuredDisplayStyle) {
  switch (displayStyle) {
    case SecuredDisplayStyle.secured:
      return messages.gettext('SECURE CONNECTION');

    case SecuredDisplayStyle.blocked:
      return messages.gettext('BLOCKED CONNECTION');

    case SecuredDisplayStyle.securing:
      return messages.gettext('CREATING SECURE CONNECTION');

    case SecuredDisplayStyle.unsecured:
      return messages.gettext('UNSECURED CONNECTION');

    case SecuredDisplayStyle.failedToSecure:
      return messages.gettext('FAILED TO SECURE CONNECTION');
  }
}
