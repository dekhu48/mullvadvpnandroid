import styled from 'styled-components';

import { colors } from '../../../config.json';
import { measurements, tinyText } from '../common-styles';
import InfoButton from '../InfoButton';

const StyledTitle = styled.h2(tinyText, {
  color: colors.white80,
  margin: `0 ${measurements.viewMargin} 8px`,
  lineHeight: '17px',
});

export const StyledSettingsGroup = styled.div({});

interface SettingsGroupProps {
  title?: string;
  infoMessage?: string | Array<string>;
}

export function SettingsGroup(props: React.PropsWithChildren<SettingsGroupProps>) {
  return (
    <>
      {props.title !== undefined && (
        <>
          <StyledTitle>{props.title}</StyledTitle>
          {props.infoMessage !== undefined && <InfoButton message={props.infoMessage} />}
        </>
      )}
      <StyledSettingsGroup>{props.children}</StyledSettingsGroup>
    </>
  );
}
