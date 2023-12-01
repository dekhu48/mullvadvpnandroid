import styled from 'styled-components';

import { colors } from '../../../config.json';
import { AriaInputGroup, AriaLabel } from '../AriaGroup';
import { measurements, smallNormalText } from '../common-styles';
import { StyledSettingsGroup } from './SettingsGroup';

const StyledSettingsRow = styled.div({
  display: 'flex',
  alignItems: 'center',

  margin: `0 ${measurements.viewMargin} ${measurements.rowVerticalMargin}`,
  padding: '0 8px',
  height: '36px',
  backgroundColor: colors.blue60,
  borderRadius: '4px',

  [`${StyledSettingsGroup} &&`]: {
    marginBottom: 0,
  },

  [`${StyledSettingsGroup} &&:not(:last-child)`]: {
    marginBottom: '1px',
    borderBottomLeftRadius: 0,
    borderBottomRightRadius: 0,
  },

  [`${StyledSettingsGroup} &&:not(:first-child)`]: {
    borderTopLeftRadius: 0,
    borderTopRightRadius: 0,
  },

  '&&:focus-within': {
    outline: `2px ${colors.white} solid`,
  },
});

const StyledLabel = styled.div(smallNormalText, {
  flex: 1,
});

const StyledInputContainer = styled.div({});

interface IndentedRowProps {
  label: string;
  infoMessage?: string | Array<string>;
}

export function SettingsRow(props: React.PropsWithChildren<IndentedRowProps>) {
  return (
    <AriaInputGroup>
      <StyledSettingsRow>
        <AriaLabel>
          <StyledLabel>{props.label}</StyledLabel>
        </AriaLabel>
        <StyledInputContainer>{props.children}</StyledInputContainer>
      </StyledSettingsRow>
    </AriaInputGroup>
  );
}
