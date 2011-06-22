package cz.muni.fi.pb138.jaro2011.videoweb;

/**
 *
 * @author Honza
 */
public enum Type {
    ORIGINAL
    {
        @Override
        public String toString()
        {
            return "originál";
        }
    }, 
    MAGAZINE
    {
        @Override
        public String toString()
        {
            return "časopis";
        }
    }, 
    HOME
    {
        @Override
        public String toString()
        {
            return "domácí";
        }
    }, 
    COPY
    {
        @Override
        public String toString()
        {
            return "kopie";
        }
    }
}

