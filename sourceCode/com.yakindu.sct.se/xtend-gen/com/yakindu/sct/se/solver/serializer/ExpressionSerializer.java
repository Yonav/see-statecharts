package com.yakindu.sct.se.solver.serializer;

import com.google.common.base.Objects;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.yakindu.base.expressions.expressions.AdditiveOperator;
import org.yakindu.base.expressions.expressions.ArgumentExpression;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.AssignmentOperator;
import org.yakindu.base.expressions.expressions.BinaryExpression;
import org.yakindu.base.expressions.expressions.BitwiseOperator;
import org.yakindu.base.expressions.expressions.BoolLiteral;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.DoubleLiteral;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.expressions.expressions.FloatLiteral;
import org.yakindu.base.expressions.expressions.HexLiteral;
import org.yakindu.base.expressions.expressions.IntLiteral;
import org.yakindu.base.expressions.expressions.Literal;
import org.yakindu.base.expressions.expressions.LogicalOperator;
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
import org.yakindu.base.expressions.expressions.NullLiteral;
import org.yakindu.base.expressions.expressions.ParenthesizedExpression;
import org.yakindu.base.expressions.expressions.RelationalOperator;
import org.yakindu.base.expressions.expressions.ShiftOperator;
import org.yakindu.base.expressions.expressions.StringLiteral;
import org.yakindu.base.expressions.expressions.UnaryExpression;
import org.yakindu.base.expressions.expressions.UnaryOperator;
import org.yakindu.base.expressions.expressions.impl.PrimitiveValueExpressionImpl;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

@SuppressWarnings("all")
public class ExpressionSerializer {
  private boolean customFunctions;
  
  public ExpressionSerializer(final boolean customFunctions) {
    this.customFunctions = customFunctions;
  }
  
  protected String _serialize(final Expression expression) {
    StringConcatenation _builder = new StringConcatenation();
    String _prettyString = this.prettyString(this.serializeExpression(expression));
    _builder.append(_prettyString);
    return _builder.toString();
  }
  
  protected String _serializeExpression(final Expression expression) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("test");
    return _builder.toString();
  }
  
  protected String _serializeExpression(final EventDefinition expression) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("test");
    return _builder.toString();
  }
  
  protected String _serializeExpression(final SequenceBlock it) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<Expression> _expressions = it.getExpressions();
      for(final Expression e : _expressions) {
        String _serializeExpression = this.serializeExpression(e);
        _builder.append(_serializeExpression);
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder.toString();
  }
  
  protected String _serializeExpression(final AssignmentExpression it) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    CharSequence _serializeOperator = this.serializeOperator(it.getOperator());
    _builder.append(_serializeOperator);
    _builder.append(" ");
    String _serializeExpression = this.serializeExpression(it.getVarRef());
    _builder.append(_serializeExpression);
    _builder.append(" ");
    String _serializeExpression_1 = this.serializeExpression(it.getExpression());
    _builder.append(_serializeExpression_1);
    _builder.append(")");
    return _builder.toString();
  }
  
  protected String _serializeExpression(final ParenthesizedExpression it) {
    StringConcatenation _builder = new StringConcatenation();
    String _serializeExpression = this.serializeExpression(it.getExpression());
    _builder.append(_serializeExpression);
    return _builder.toString();
  }
  
  protected String _serializeExpression(final PrimitiveValueExpressionImpl it) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _serializeLiteral = this.serializeLiteral(it.getValue());
    _builder.append(_serializeLiteral);
    return _builder.toString();
  }
  
  protected String _serializeExpression(final ElementReferenceExpression it) {
    StringConcatenation _builder = new StringConcatenation();
    String _serializeExpression = this.serializeExpression(it.getReference());
    _builder.append(_serializeExpression);
    return _builder.toString();
  }
  
  protected String _serializeExpression(final VariableDefinition it) {
    StringConcatenation _builder = new StringConcatenation();
    String _name = it.getName();
    _builder.append(_name);
    return _builder.toString();
  }
  
  protected String _serializeExpression(final ArgumentExpression it) {
    throw new IllegalArgumentException("Isn\'t implemented");
  }
  
  protected String _serializeExpression(final ConditionalExpression it) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(ite ");
    String _serializeExpression = this.serializeExpression(it.getCondition());
    _builder.append(_serializeExpression);
    _builder.append(" ");
    String _serializeExpression_1 = this.serializeExpression(it.getTrueCase());
    _builder.append(_serializeExpression_1);
    _builder.append(" ");
    String _serializeExpression_2 = this.serializeExpression(it.getFalseCase());
    _builder.append(_serializeExpression_2);
    _builder.append(")");
    return _builder.toString();
  }
  
  protected String _serializeExpression(final UnaryExpression it) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("(");
    CharSequence _serializeOperator = this.serializeOperator(it.getOperator());
    _builder.append(_serializeOperator);
    _builder.append(" ");
    String _serializeExpression = this.serializeExpression(it.getOperand());
    _builder.append(_serializeExpression);
    _builder.append(")");
    return _builder.toString();
  }
  
  protected String _serializeExpression(final BinaryExpression it) {
    String _xifexpression = null;
    Enumerator _operator = it.getOperator();
    boolean _equals = Objects.equal(_operator, MultiplicativeOperator.DIV);
    if (_equals) {
      String _xifexpression_1 = null;
      if (this.customFunctions) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("(yakDiv ");
        String _serializeExpression = this.serializeExpression(it.getLeftOperand());
        _builder.append(_serializeExpression);
        _builder.append(" ");
        String _serializeExpression_1 = this.serializeExpression(it.getRightOperand());
        _builder.append(_serializeExpression_1);
        _builder.append(")");
        _xifexpression_1 = _builder.toString();
      } else {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("(");
        CharSequence _serializeOperator = this.serializeOperator(it.getOperator());
        _builder_1.append(_serializeOperator);
        _builder_1.append(" ");
        String _serializeExpression_2 = this.serializeExpression(it.getLeftOperand());
        _builder_1.append(_serializeExpression_2);
        _builder_1.append(" ");
        String _serializeExpression_3 = this.serializeExpression(it.getRightOperand());
        _builder_1.append(_serializeExpression_3);
        _builder_1.append(")");
        _xifexpression_1 = _builder_1.toString();
      }
      _xifexpression = _xifexpression_1;
    } else {
      String _xifexpression_2 = null;
      int _value = it.getOperator().getValue();
      boolean _equals_1 = (_value == RelationalOperator.NOT_EQUALS_VALUE);
      if (_equals_1) {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("(not (= ");
        String _serializeExpression_4 = this.serializeExpression(it.getLeftOperand());
        _builder_2.append(_serializeExpression_4);
        _builder_2.append(" ");
        String _serializeExpression_5 = this.serializeExpression(it.getRightOperand());
        _builder_2.append(_serializeExpression_5);
        _builder_2.append("))");
        _xifexpression_2 = _builder_2.toString();
      } else {
        StringConcatenation _builder_3 = new StringConcatenation();
        _builder_3.append("(");
        CharSequence _serializeOperator_1 = this.serializeOperator(it.getOperator());
        _builder_3.append(_serializeOperator_1);
        _builder_3.append(" ");
        String _serializeExpression_6 = this.serializeExpression(it.getLeftOperand());
        _builder_3.append(_serializeExpression_6);
        _builder_3.append(" ");
        String _serializeExpression_7 = this.serializeExpression(it.getRightOperand());
        _builder_3.append(_serializeExpression_7);
        _builder_3.append(")");
        _xifexpression_2 = _builder_3.toString();
      }
      _xifexpression = _xifexpression_2;
    }
    return _xifexpression;
  }
  
  protected CharSequence _serializeLiteral(final Literal it) {
    return null;
  }
  
  protected CharSequence _serializeLiteral(final BoolLiteral it) {
    StringConcatenation _builder = new StringConcatenation();
    boolean _isValue = it.isValue();
    _builder.append(_isValue);
    return _builder;
  }
  
  protected CharSequence _serializeLiteral(final IntLiteral it) {
    CharSequence _xifexpression = null;
    int _value = it.getValue();
    boolean _lessThan = (_value < 0);
    if (_lessThan) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("(- ");
      int _value_1 = it.getValue();
      int _minus = (-_value_1);
      _builder.append(_minus);
      _builder.append(")");
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      int _value_2 = it.getValue();
      _builder_1.append(_value_2);
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  protected CharSequence _serializeLiteral(final DoubleLiteral it) {
    StringConcatenation _builder = new StringConcatenation();
    double _value = it.getValue();
    _builder.append(_value);
    return _builder;
  }
  
  protected CharSequence _serializeLiteral(final FloatLiteral it) {
    StringConcatenation _builder = new StringConcatenation();
    float _value = it.getValue();
    _builder.append(_value);
    return _builder;
  }
  
  protected CharSequence _serializeLiteral(final HexLiteral it) {
    StringConcatenation _builder = new StringConcatenation();
    int _value = it.getValue();
    _builder.append(_value);
    return _builder;
  }
  
  protected CharSequence _serializeLiteral(final StringLiteral it) {
    StringConcatenation _builder = new StringConcatenation();
    String _value = it.getValue();
    _builder.append(_value);
    return _builder;
  }
  
  protected CharSequence _serializeLiteral(final NullLiteral it) {
    throw new IllegalArgumentException("Isn\'t implemented");
  }
  
  protected CharSequence _serializeOperator(final org.yakindu.base.types.Enumerator it) {
    throw new IllegalArgumentException("Implement me");
  }
  
  protected CharSequence _serializeOperator(final BitwiseOperator it) {
    StringConcatenation _builder = new StringConcatenation();
    String _string = it.toString();
    _builder.append(_string);
    return _builder;
  }
  
  protected CharSequence _serializeOperator(final RelationalOperator it) {
    CharSequence _switchResult = null;
    int _value = it.getValue();
    switch (_value) {
      case RelationalOperator.EQUALS_VALUE:
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("=");
        _switchResult = _builder;
        break;
      case RelationalOperator.NOT_EQUALS_VALUE:
        throw new NullPointerException("distinct value..");
      default:
        StringConcatenation _builder_1 = new StringConcatenation();
        String _string = it.toString();
        _builder_1.append(_string);
        _switchResult = _builder_1;
        break;
    }
    return _switchResult;
  }
  
  protected CharSequence _serializeOperator(final LogicalOperator it) {
    CharSequence _switchResult = null;
    int _value = it.getValue();
    switch (_value) {
      case LogicalOperator.AND_VALUE:
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("and");
        _switchResult = _builder;
        break;
      case LogicalOperator.OR_VALUE:
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("or");
        _switchResult = _builder_1;
        break;
      case LogicalOperator.NOT_VALUE:
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("not");
        _switchResult = _builder_2;
        break;
    }
    return _switchResult;
  }
  
  protected CharSequence _serializeOperator(final AdditiveOperator it) {
    StringConcatenation _builder = new StringConcatenation();
    String _string = it.toString();
    _builder.append(_string);
    return _builder;
  }
  
  protected CharSequence _serializeOperator(final MultiplicativeOperator it) {
    CharSequence _switchResult = null;
    int _value = it.getValue();
    switch (_value) {
      case MultiplicativeOperator.DIV_VALUE:
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("div");
        _switchResult = _builder;
        break;
      case MultiplicativeOperator.MOD_VALUE:
        CharSequence _xifexpression = null;
        if (this.customFunctions) {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("mod");
          _xifexpression = _builder_1;
        } else {
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("%");
          _xifexpression = _builder_2;
        }
        _switchResult = _xifexpression;
        break;
      default:
        StringConcatenation _builder_3 = new StringConcatenation();
        String _string = it.toString();
        _builder_3.append(_string);
        _switchResult = _builder_3;
        break;
    }
    return _switchResult;
  }
  
  protected CharSequence _serializeOperator(final AssignmentOperator it) {
    CharSequence _switchResult = null;
    int _value = it.getValue();
    switch (_value) {
      case AssignmentOperator.ASSIGN_VALUE:
        StringConcatenation _builder = new StringConcatenation();
        _builder.append(it);
        _switchResult = _builder;
        break;
      default:
        throw new IllegalArgumentException("Isn\'t implemented");
    }
    return _switchResult;
  }
  
  protected CharSequence _serializeOperator(final UnaryOperator it) {
    StringConcatenation _builder = new StringConcatenation();
    String _string = it.toString();
    _builder.append(_string);
    return _builder;
  }
  
  protected CharSequence _serializeOperator(final ShiftOperator it) {
    StringConcatenation _builder = new StringConcatenation();
    String _string = it.toString();
    _builder.append(_string);
    return _builder;
  }
  
  private String prettyString(final String it) {
    StringConcatenation _builder = new StringConcatenation();
    String _replace = it.replace("#", "-hash-").replace("$", "-dollar-");
    _builder.append(_replace);
    return _builder.toString();
  }
  
  public String serialize(final Expression expression) {
    return _serialize(expression);
  }
  
  public String serializeExpression(final EObject it) {
    if (it instanceof PrimitiveValueExpressionImpl) {
      return _serializeExpression((PrimitiveValueExpressionImpl)it);
    } else if (it instanceof EventDefinition) {
      return _serializeExpression((EventDefinition)it);
    } else if (it instanceof VariableDefinition) {
      return _serializeExpression((VariableDefinition)it);
    } else if (it instanceof ElementReferenceExpression) {
      return _serializeExpression((ElementReferenceExpression)it);
    } else if (it instanceof SequenceBlock) {
      return _serializeExpression((SequenceBlock)it);
    } else if (it instanceof ArgumentExpression) {
      return _serializeExpression((ArgumentExpression)it);
    } else if (it instanceof AssignmentExpression) {
      return _serializeExpression((AssignmentExpression)it);
    } else if (it instanceof BinaryExpression) {
      return _serializeExpression((BinaryExpression)it);
    } else if (it instanceof ConditionalExpression) {
      return _serializeExpression((ConditionalExpression)it);
    } else if (it instanceof ParenthesizedExpression) {
      return _serializeExpression((ParenthesizedExpression)it);
    } else if (it instanceof UnaryExpression) {
      return _serializeExpression((UnaryExpression)it);
    } else if (it instanceof Expression) {
      return _serializeExpression((Expression)it);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(it).toString());
    }
  }
  
  public CharSequence serializeLiteral(final Literal it) {
    if (it instanceof HexLiteral) {
      return _serializeLiteral((HexLiteral)it);
    } else if (it instanceof BoolLiteral) {
      return _serializeLiteral((BoolLiteral)it);
    } else if (it instanceof DoubleLiteral) {
      return _serializeLiteral((DoubleLiteral)it);
    } else if (it instanceof FloatLiteral) {
      return _serializeLiteral((FloatLiteral)it);
    } else if (it instanceof IntLiteral) {
      return _serializeLiteral((IntLiteral)it);
    } else if (it instanceof NullLiteral) {
      return _serializeLiteral((NullLiteral)it);
    } else if (it instanceof StringLiteral) {
      return _serializeLiteral((StringLiteral)it);
    } else if (it != null) {
      return _serializeLiteral(it);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(it).toString());
    }
  }
  
  public CharSequence serializeOperator(final Object it) {
    if (it instanceof org.yakindu.base.types.Enumerator) {
      return _serializeOperator((org.yakindu.base.types.Enumerator)it);
    } else if (it instanceof AdditiveOperator) {
      return _serializeOperator((AdditiveOperator)it);
    } else if (it instanceof AssignmentOperator) {
      return _serializeOperator((AssignmentOperator)it);
    } else if (it instanceof BitwiseOperator) {
      return _serializeOperator((BitwiseOperator)it);
    } else if (it instanceof LogicalOperator) {
      return _serializeOperator((LogicalOperator)it);
    } else if (it instanceof MultiplicativeOperator) {
      return _serializeOperator((MultiplicativeOperator)it);
    } else if (it instanceof RelationalOperator) {
      return _serializeOperator((RelationalOperator)it);
    } else if (it instanceof ShiftOperator) {
      return _serializeOperator((ShiftOperator)it);
    } else if (it instanceof UnaryOperator) {
      return _serializeOperator((UnaryOperator)it);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(it).toString());
    }
  }
}
