import React, { Component } from 'react';
import {MapTo} from '@adobe/aem-react-editable-components';

import sanitizeHtml from 'sanitize-html';
import sanitizeWhiteList from '../../sanitize-html.whitelist';

require('./Text.css');

export default class Text extends Component {
  render() {
    return (
      <div
        id={(this.props.htmlID).replace(/[^A-Z^a-z^0-9]+/g, '')}
        dangerouslySetInnerHTML={{__html: sanitizeHtml(this.props.richtext, sanitizeWhiteList)}} 
      />
    );
  }
}

const EditConfig = {
  emptyLabel: 'Text',

  isEmpty: function (props) {
      return !props || !props.richtext || props.richtext.trim().length < 1;
  }
};

MapTo('aem/components/content/text')(Text, EditConfig);